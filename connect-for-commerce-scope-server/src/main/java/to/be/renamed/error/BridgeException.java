package to.be.renamed.error;

/**
 * Runtime Exception to be thrown when having general errors with bridge.
 */
public class BridgeException extends RuntimeException {

    private final String errorCode;

    /**
     * Creates a BridgeConnectionException
     *
     * @param message   The message of the exception
     * @param errorCode The error code of the error
     */
    public BridgeException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
