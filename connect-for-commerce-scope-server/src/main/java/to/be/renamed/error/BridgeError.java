package to.be.renamed.error;

import kong.unirest.json.JSONObject;

import java.io.Serializable;

/**
 * Representation of an error returned in the bridges response
 */
public class BridgeError implements Serializable {
    private static final long serialVersionUID = -3652573135969314029L;
    private static final String FIELD_PROPERTY = "field";
    private static final String CAUSE_PROPERTY = "cause";
    private static final String CODE_PROPERTY = "code";
    private static final String UNKNOWN = "unknown";
    private final String field;
    private final String cause;
    private final String code;

    /**
     * Creates a representation of an error returned in the bridges response
     * @param jsonObject The error as a unirest JSONObject
     */
    public BridgeError(JSONObject jsonObject) {
        if (jsonObject.has(FIELD_PROPERTY) && jsonObject.get(FIELD_PROPERTY) instanceof String) {
            this.field = (String) jsonObject.get(FIELD_PROPERTY);
        } else {
            this.field = UNKNOWN;
        }
        if (jsonObject.has(CAUSE_PROPERTY) && jsonObject.get(CAUSE_PROPERTY) instanceof String) {
            this.cause = (String) jsonObject.get(CAUSE_PROPERTY);
        } else {
            this.cause = UNKNOWN;
        }
        if (jsonObject.has(CODE_PROPERTY) && jsonObject.get(CODE_PROPERTY) instanceof String) {
            this.code = (String) jsonObject.get(CODE_PROPERTY);
        } else {
            this.code = ErrorCode.UNKNOWN.get();
        }
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }
}
