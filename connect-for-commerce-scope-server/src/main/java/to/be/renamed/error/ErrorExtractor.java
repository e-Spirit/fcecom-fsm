package to.be.renamed.error;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for extracting errors from Json bodies
 */
public final class ErrorExtractor {
    private ErrorExtractor() {}

    /**
     * Extracts BodyValidationErrors from an unirest json body
     * @param body Response body as unirest JsonNode
     * @return A list of BodyValidationErrors
     */
    public static List<BridgeError> extractBodyValidationErrors(JsonNode body) {
        List<BridgeError> bridgeErrors = new ArrayList<>();
        if (body.getObject().has("error")) {
            JSONArray errors = body.getObject().getJSONArray("error");
            for (int i = 0; i < errors.length(); i++) {
                JSONObject error = errors.getJSONObject(i);
                bridgeErrors.add(new BridgeError(error));
            }
        }
        return bridgeErrors;
    }


}
