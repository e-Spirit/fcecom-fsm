package to.be.renamed.error;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    public static List<BridgeError> extractBodyValidationErrors(JsonElement body) {
        List<BridgeError> bridgeErrors = new ArrayList<>();
        if (body.isJsonObject() && body.getAsJsonObject().has("error")) {
            JsonArray errors = body.getAsJsonObject().get("error").getAsJsonArray();
            for (int i = 0; i < errors.size(); i++) {
                JsonObject error = errors.get(i).getAsJsonObject();
                bridgeErrors.add(new BridgeError(error));
            }
        }
        return bridgeErrors;
    }


}
