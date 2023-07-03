package to.be.renamed.bridge.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import com.google.gson.JsonPrimitive;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.json.values.JsonBooleanValue;
import de.espirit.firstspirit.json.values.JsonNullValue;
import de.espirit.firstspirit.json.values.JsonNumberValue;
import de.espirit.firstspirit.json.values.JsonStringValue;

import java.util.Map;

import static java.lang.String.format;

public class Json {


    private final JsonObject source;

    public Json() {
        this(new JsonObject());
    }

    public Json(JsonObject source) {
        this.source = source;
    }

    public Json(JsonElement jsonElement) {
        this.source = jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : new JsonObject();
    }

    public JsonObject getValue() {
        return source;
    }

    @Override
    public String toString() {
        return format("%s %s", getClass().getSimpleName(), source.toString());
    }

    public String get(String path) {
        JsonElement element = source.get(path);
        String value = element == null || element.equals(JsonNull.INSTANCE) ? null : String.valueOf(element.getAsString());
        return Strings.isEmpty(value) || "null".equalsIgnoreCase(value) ? null : value;
    }

    public static JsonElement asJsonElement(Object input) {
        if (input instanceof Boolean) {
            return new JsonPrimitive((Boolean) input);
        } else if (input instanceof Number) {
            return new JsonPrimitive((Number) input);
        } else if (input instanceof String) {
            return new JsonPrimitive((String) input);
        } else if (input instanceof Iterable<?>) {
            JsonArray jsonArray = new JsonArray();
            ((Iterable<?>) input).forEach(item -> jsonArray.add(asJsonElement(item)));
            return jsonArray;
        } else if (input instanceof Map) {
            JsonObject jsonObject = new JsonObject();
            ((Map<?, ?>) input).forEach((key, value) -> jsonObject.add(String.valueOf(key), asJsonElement(value)));
            return jsonObject;
        } else {
            if (input != null)
                Logging.logWarning(format("Unable to transform %s (%s)", input, input.getClass().getName()), Json.class);
            return JsonNull.INSTANCE;
        }
    }

    public static de.espirit.firstspirit.json.JsonElement<?> asFSJsonElement(Object input) {
        if (input instanceof Boolean) {
            return JsonBooleanValue.of((Boolean) input);
        } else if (input instanceof Number) {
            return JsonNumberValue.of((Number) input);
        } else if (input instanceof String) {
            return JsonStringValue.of((String) input);
        } else if (input instanceof Iterable<?>) {
            de.espirit.firstspirit.json.JsonArray jsonArray = de.espirit.firstspirit.json.JsonArray.create();
            ((Iterable<?>) input).forEach(item -> jsonArray.add(asFSJsonElement(item)));
            return jsonArray;
        } else if (input instanceof Map) {
            de.espirit.firstspirit.json.JsonObject jsonObject = de.espirit.firstspirit.json.JsonObject.create();
            ((Map<?, ?>) input).forEach((key, value) -> jsonObject.put(String.valueOf(key), asFSJsonElement(value)));
            return jsonObject;
        } else {
            if (input != null)
                Logging.logWarning(format("Unable to transform %s (%s)", input, input.getClass().getName()), Json.class);
            return JsonNullValue.NULL;
        }
    }
}
