package to.be.renamed.executable;

import static java.lang.String.format;

public class RequiredParamMissingException extends IllegalArgumentException {
    /**
     * Creates a RequiredParamMissingException
     *
     * @param parameterName The name of the parameter that is missing.
     */
    public RequiredParamMissingException(String parameterName) {
        super(format("The required parameterName '%s' is missing", parameterName));
    }
}
