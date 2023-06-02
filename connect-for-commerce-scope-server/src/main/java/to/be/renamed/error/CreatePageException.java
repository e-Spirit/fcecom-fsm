package to.be.renamed.error;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

/**
 * Runtime Exception to be thrown when errors occur during FS Driven or Shop Driven page creation
 */
public class CreatePageException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = 3081345535677593817L;
    private final List<BridgeError> bridgeErrors;
    private final String errorCode;

    /**
     * Creates a CreatePageException
     * @param message The message of the exception
     * @param bridgeErrors A list of BridgeErrors
     * @param errorCode The error code of the error
     */
    public CreatePageException(String message, @Nullable List<BridgeError> bridgeErrors, @Nullable String errorCode) {
        super(message);
        this.bridgeErrors = Collections.unmodifiableList(Objects.requireNonNullElseGet(bridgeErrors, ArrayList::new));
        this.errorCode = errorCode;
    }

    public List<BridgeError> getBridgeErrors() {
        return bridgeErrors;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
