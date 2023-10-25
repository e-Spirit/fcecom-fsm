package to.be.renamed.error;

/**
 * Enum for holding error codes
 */
public enum ErrorCode {
    UNKNOWN("0000"),
    // FSM Errors
    PAGE_ALREADY_EXISTS("2010"),
    TEMPLATE_NOT_FOUND("2020"),
    INCORRECT_PAGE_TYPE("2030"),
    INVALID_DISPLAYNAMES_FORMAT("2040"),
    REQUIRED_PARAM_MISSING("2050"),
    // Bridge connection errors
    CANNOT_CONNECT_TO_BRIDGE("3010"),
    BRIDGE_AUTH_ERROR("3020"),
    BRIDGE_SERVER_ERROR("3030"),
    BRIDGE_REQUEST_TIMEOUT("3040"),
    // CC extension errors 
    MISSING_CC_EXTENSION("3050"),
    // Shop system connection errors
    CANNOT_CONNECT_TO_SHOP("4010"),
    SHOP_AUTH_ERROR("4020");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    /**
     * Getter for stored error code
     *
     * @return The error code as a string
     */
    public String get() {
        return code;
    }
}
