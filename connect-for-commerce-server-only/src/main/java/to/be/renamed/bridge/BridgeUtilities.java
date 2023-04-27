package to.be.renamed.bridge;

import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.JsonPair;
import kong.unirest.json.JSONObject;

public final class BridgeUtilities {
    private BridgeUtilities() {}

    public static JSONObject toJSONObject(JsonObject input) {
        JSONObject jsonObject = new JSONObject();
        for (JsonPair pair : input.pairs()) {
            Object value = pair.getValue().getValue();
            if (value instanceof JsonObject) {
                jsonObject.put(pair.getKey(), toJSONObject((JsonObject) value));
            } else if (value instanceof String) {
                jsonObject.put(pair.getKey(), value);
            } else if (value instanceof Number) {
                jsonObject.put(pair.getKey(), value);
            } else if (value instanceof Boolean) {
                jsonObject.put(pair.getKey(), value);
            }
        }
        return jsonObject;
    }
}
