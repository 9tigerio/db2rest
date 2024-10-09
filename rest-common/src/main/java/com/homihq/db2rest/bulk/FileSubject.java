package com.homihq.db2rest.bulk;

import java.io.InputStream;

public interface FileSubject {
    void register(FileStreamObserver observer);
    void startStreaming(InputStream inputStream);
    void unregister();
}
