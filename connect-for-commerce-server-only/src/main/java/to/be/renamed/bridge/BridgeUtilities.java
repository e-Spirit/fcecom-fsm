package to.be.renamed.bridge;

import to.be.renamed.bridge.client.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.espirit.common.base.Logging;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import java.util.Map;

import static java.lang.String.format;

public final class BridgeUtilities {
    private BridgeUtilities() {}

    public static JSONObject toJSONObject(JsonObject input) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, ?> element : input.entrySet()) {
            Object value = element.getValue();
            if (value instanceof JsonObject) {
                jsonObject.put(element.getKey(), toJSONObject((JsonObject) value));
            } else if (value instanceof JsonPrimitive && ((JsonPrimitive) value).isString()) {
                jsonObject.put(element.getKey(), ((JsonPrimitive) value).getAsString());
            } else if (value instanceof JsonPrimitive && ((JsonPrimitive) value).isNumber()) {
                jsonObject.put(element.getKey(), ((JsonPrimitive) value).getAsNumber());
            } else if (value instanceof JsonPrimitive && ((JsonPrimitive) value).isBoolean()) {
                jsonObject.put(element.getKey(), ((JsonPrimitive) value).getAsBoolean());
            }
        }
        return jsonObject;
    }

    public static JsonElement toJsonElement(Object input) {
        if (input instanceof JsonNode && ((JsonNode) input).isArray()) {
            JsonArray jsonArray = new JsonArray();
            ((JsonNode) input).getArray().forEach(item -> jsonArray.add(toJsonElement(item)));
            return jsonArray;
        } else if (input instanceof JSONObject) {
            JsonObject jsonObject = new JsonObject();
            JSONObject object = (JSONObject) input;
            for (String key : object.keySet())
                jsonObject.add(key, toJsonElement(object.get(key)));
            return jsonObject;
        } else if (input instanceof Boolean) {
            return new JsonPrimitive((Boolean) input);
        } else if (input instanceof Number) {
            return new JsonPrimitive((Number) input);
        } else if (input instanceof String) {
            return new JsonPrimitive((String) input);
        } else if (input instanceof Iterable<?>) {
            JsonArray jsonArray = new JsonArray();
            ((Iterable<?>) input).forEach(item -> jsonArray.add(toJsonElement(item)));
            return jsonArray;
        } else if (input instanceof Map) {
            JsonObject jsonObject = new JsonObject();
            ((Map<?, ?>) input).forEach((key, value) -> jsonObject.add(String.valueOf(key), toJsonElement(value)));
            return jsonObject;
        } else {
            if (input != null)
                Logging.logWarning(format("Unable to transform %s (%s)", input, input.getClass().getName()), Json.class);
            return JsonNull.INSTANCE;
        }
    }
}
