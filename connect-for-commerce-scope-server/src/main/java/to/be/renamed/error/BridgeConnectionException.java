package to.be.renamed.error;

/**
 * Runtime Exception to be thrown when having errors with bridge connection
 */
public class BridgeConnectionException extends RuntimeException {
    private final String errorCode;

    /**
     * Creates a BridgeConnectionException
     * @param message The message of the exception
     * @param errorCode The error code of the error
     */
    public BridgeConnectionException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
