package com.homihq.db2rest.core.exception;

/**
 * Raised when a multipart JSON upload fails.
 * <p>
 * An upload runs in a single transaction, so a failure always means nothing from
 * the file was written. Every message ends with a sentence that says so, which
 * gives clients one stable contract to rely on. Messages never contain database
 * error text; the original error, if any, is kept as the cause for logging.
 */
public class BulkUploadException extends GenericDataAccessException {

    public static final String NOTHING_COMMITTED = "No rows were committed.";

    public BulkUploadException(String reason) {
        super(reason + " " + NOTHING_COMMITTED);
    }

    public BulkUploadException(String reason, Throwable cause) {
        super(reason + " " + NOTHING_COMMITTED, cause);
    }

    public static BulkUploadException emptyFile() {
        return new BulkUploadException("Upload file is empty.");
    }

    public static BulkUploadException notAnArray() {
        return new BulkUploadException("Upload file must contain a JSON array of objects.");
    }

    public static BulkUploadException noRows() {
        return new BulkUploadException("Upload file contains no rows.");
    }

    public static BulkUploadException malformed(long line, long column, Throwable cause) {
        return new BulkUploadException(
                "Upload file is not valid JSON (line " + line + ", column " + column + ").", cause);
    }

    public static BulkUploadException notAnObject(long index) {
        return new BulkUploadException("Element " + index + " of the upload array is not a JSON object.");
    }

    public static BulkUploadException trailingContent() {
        return new BulkUploadException("Upload file has content after the end of the JSON array.");
    }

    public static BulkUploadException rejected(long fromIndex, long toIndex, Throwable cause) {
        return new BulkUploadException(
                "The database rejected the rows at array index " + fromIndex + " to " + toIndex + ".", cause);
    }

    public static BulkUploadException timedOut(int seconds, Throwable cause) {
        return new BulkUploadException("Upload did not finish within " + seconds + " seconds.", cause);
    }

    public static BulkUploadException cancelled() {
        return new BulkUploadException("Upload was cancelled.");
    }

    public static BulkUploadException unreadable(Throwable cause) {
        return new BulkUploadException("Upload file could not be read.", cause);
    }

    public static BulkUploadException commitFailed(Throwable cause) {
        return new BulkUploadException("Upload could not be committed.", cause);
    }
}
