package to.be.renamed.error;

import com.google.gson.JsonObject;

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
     *
     * @param jsonObject The error as a unirest JSONObject
     */
    public BridgeError(JsonObject jsonObject) {
        if (jsonObject.has(FIELD_PROPERTY) && jsonObject.get(FIELD_PROPERTY).isJsonPrimitive()) {
            this.field = jsonObject.get(FIELD_PROPERTY).getAsString();
        } else {
            this.field = UNKNOWN;
        }
        if (jsonObject.has(CAUSE_PROPERTY) && jsonObject.get(CAUSE_PROPERTY).isJsonPrimitive()) {
            this.cause = jsonObject.get(CAUSE_PROPERTY).getAsString();
        } else {
            this.cause = UNKNOWN;
        }
        if (jsonObject.has(CODE_PROPERTY) && jsonObject.get(CODE_PROPERTY).isJsonPrimitive()) {
            this.code = jsonObject.get(CODE_PROPERTY).getAsString();
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
