package to.be.renamed.executable;

public class RequiredParamMissingException extends IllegalArgumentException {
    /**
     * Creates a RequiredParamMissingException
     *
     * @param message The message of the exception
     */
    public RequiredParamMissingException(String message) {
        super(message);
    }
}
