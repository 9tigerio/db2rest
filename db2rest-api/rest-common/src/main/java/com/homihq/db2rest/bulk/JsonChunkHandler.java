package com.homihq.db2rest.bulk;

import java.util.List;
import java.util.Map;

/**
 * Receives the rows of a JSON upload one chunk at a time.
 * <p>
 * A handler is created for a single upload, so it can safely capture that
 * upload's destination and counters.
 */
@FunctionalInterface
public interface JsonChunkHandler {

    /**
     * @param rows       the rows of this chunk, never empty
     * @param firstIndex the array index of the first row in the chunk
     */
    void onChunk(List<Map<String, Object>> rows, long firstIndex);
}
