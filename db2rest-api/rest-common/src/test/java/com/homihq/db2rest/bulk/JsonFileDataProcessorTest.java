package com.homihq.db2rest.bulk;

import com.homihq.db2rest.core.exception.BulkUploadException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonFileDataProcessorTest {

    private final JsonFileDataProcessor processor = new JsonFileDataProcessor();

    @Test
    void splitsArrayIntoChunksWithStartIndexes() throws Exception {
        List<Integer> sizes = new ArrayList<>();
        List<Long> starts = new ArrayList<>();

        long rows = processor.stream(stream(array(0, 7)), 3, (chunk, first) -> {
            sizes.add(chunk.size());
            starts.add(first);
        });

        assertThat(rows).isEqualTo(7);
        assertThat(sizes).containsExactly(3, 3, 1);
        assertThat(starts).containsExactly(0L, 3L, 6L);
    }

    @Test
    void exactMultipleOfChunkSizeHasNoEmptyTail() throws Exception {
        List<Integer> sizes = new ArrayList<>();
        processor.stream(stream(array(0, 6)), 3, (chunk, first) -> sizes.add(chunk.size()));
        assertThat(sizes).containsExactly(3, 3);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n\t"})
    void emptyInputIsRejected(String content) {
        assertThatThrownBy(() -> processor.stream(stream(content), 3, (c, f) -> { }))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file is empty.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"a\":1}", "\"text\"", "42", "null"})
    void nonArrayIsRejected(String content) {
        assertThatThrownBy(() -> processor.stream(stream(content), 3, (c, f) -> { }))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file must contain a JSON array of objects.");
    }

    @Test
    void emptyArrayIsRejected() {
        assertThatThrownBy(() -> processor.stream(stream("[ ]"), 3, (c, f) -> { }))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file contains no rows.");
    }

    @Test
    void nonObjectElementIsRejected() {
        assertThatThrownBy(() -> processor.stream(stream("[{\"a\":1}, 2]"), 3, (c, f) -> { }))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Element 1 of the upload array is not a JSON object.");
    }

    @Test
    void trailingContentIsRejected() {
        assertThatThrownBy(() -> processor.stream(stream("[{\"a\":1}] {\"b\":2}"), 3, (c, f) -> { }))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file has content after the end of the JSON array.");
    }

    @Test
    void malformedJsonBeforeFirstChunkBoundaryNeverReachesHandler() {
        List<Integer> sizes = new ArrayList<>();
        String content = "[{\"id\":0},{\"id\":1},{\"id\": oops}]";

        assertThatThrownBy(() -> processor.stream(stream(content), 3, (c, f) -> sizes.add(c.size())))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file is not valid JSON (line 1, column")
                .hasMessageEndingWith(BulkUploadException.NOTHING_COMMITTED);
        assertThat(sizes).isEmpty();
    }

    @Test
    void malformedJsonAfterChunkBoundaryIsPropagated() {
        List<Integer> sizes = new ArrayList<>();
        String content = array(0, 4).replaceAll("]$", ",{\"id\":");

        assertThatThrownBy(() -> processor.stream(stream(content), 3, (c, f) -> sizes.add(c.size())))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file is not valid JSON");
        assertThat(sizes).containsExactly(3);
    }

    @Test
    void truncatedArrayIsRejected() {
        assertThatThrownBy(() -> processor.stream(stream("[{\"id\":0},"), 3, (c, f) -> { }))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file is not valid JSON");
    }

    @Test
    void streamIsClosedOnSuccess() throws Exception {
        TrackingStream in = new TrackingStream(array(0, 2));
        processor.stream(in, 3, (c, f) -> { });
        assertThat(in.closed).isTrue();
    }

    @Test
    void streamIsClosedWhenJsonIsMalformed() {
        TrackingStream in = new TrackingStream("[{\"id\":");
        assertThatThrownBy(() -> processor.stream(in, 3, (c, f) -> { })).isInstanceOf(BulkUploadException.class);
        assertThat(in.closed).isTrue();
    }

    @Test
    void streamIsClosedWhenHandlerFails() {
        TrackingStream in = new TrackingStream(array(0, 5));
        assertThatThrownBy(() -> processor.stream(in, 3, (c, f) -> {
            throw new IllegalStateException("callback failed");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(in.closed).isTrue();
    }

    @Test
    @Timeout(30)
    void oneInstanceServesOverlappingStreams() throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(2);
        List<Object> seenByA = new CopyOnWriteArrayList<>();
        List<Object> seenByB = new CopyOnWriteArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            for (int round = 0; round < 20; round++) {
                seenByA.clear();
                seenByB.clear();
                Future<Long> a = pool.submit(() -> processor.stream(stream(array("A", 0, 10)), 2, (chunk, first) -> {
                    await(barrier);
                    chunk.forEach(r -> seenByA.add(r.get("src")));
                }));
                Future<Long> b = pool.submit(() -> processor.stream(stream(array("B", 0, 10)), 2, (chunk, first) -> {
                    await(barrier);
                    chunk.forEach(r -> seenByB.add(r.get("src")));
                }));

                assertThat(a.get(10, TimeUnit.SECONDS)).isEqualTo(10);
                assertThat(b.get(10, TimeUnit.SECONDS)).isEqualTo(10);
                assertThat(seenByA).hasSize(10).containsOnly("A");
                assertThat(seenByB).hasSize(10).containsOnly("B");
            }
        } finally {
            pool.shutdownNow();
        }
    }

    private static void await(CyclicBarrier barrier) {
        try {
            barrier.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String array(int from, int to) {
        return array("x", from, to);
    }

    private static String array(String src, int from, int to) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = from; i < to; i++) {
            if (i > from) {
                sb.append(',');
            }
            sb.append("{\"id\":").append(i).append(",\"src\":\"").append(src).append("\"}");
        }
        return sb.append(']').toString();
    }

    private static InputStream stream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private static final class TrackingStream extends ByteArrayInputStream {
        private volatile boolean closed;

        TrackingStream(String content) {
            super(content.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
