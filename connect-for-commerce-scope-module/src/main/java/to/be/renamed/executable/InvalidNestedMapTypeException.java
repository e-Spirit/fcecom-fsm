package to.be.renamed.executable;

import static java.lang.String.format;

public class InvalidNestedMapTypeException extends RuntimeException {

    /**
     * Creates an InvalidNestedMapTypeException
     *
     * @param parameterName The name of the nested parameters that is not a map.
     */
    public InvalidNestedMapTypeException(String parameterName) {
        super(format("Nested parameter '%s' is not a map", parameterName));
    }
}
