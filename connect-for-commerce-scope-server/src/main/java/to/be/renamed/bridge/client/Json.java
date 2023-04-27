package to.be.renamed.bridge.client;

import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.json.JsonArray;
import de.espirit.firstspirit.json.JsonElement;
import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.values.JsonBooleanValue;
import de.espirit.firstspirit.json.values.JsonDateValue;
import de.espirit.firstspirit.json.values.JsonNullValue;
import de.espirit.firstspirit.json.values.JsonNumberValue;
import de.espirit.firstspirit.json.values.JsonStringValue;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import static java.lang.String.format;

public class Json implements JsonElement<JsonObject> {


    private final JsonObject source;

    public Json() {
        this(JsonObject.create());
    }

    public Json(JsonObject source) {
        this.source = source;
    }

    public Json(JsonElement<?> jsonElement) {
        this.source = jsonElement instanceof JSONObject ? (JsonObject) jsonElement : JsonObject.create();
    }

    @Override
    public String toString() {
        return format("%s %s", getClass().getSimpleName(), source.json());
    }

    @Override
    public JsonObject getValue() {
        return source;
    }

    @Override
    public void writeTo(Writer out) throws IOException {
        source.writeTo(out);
    }

    @Override
    public String json() {
        return source.json();
    }

    public String get(String path) {
        JsonElement<?> element = source.resolve(path);
        String value = element == null || element.equals(JsonNullValue.NULL) ? null : String.valueOf(element.getValue());
        return Strings.isEmpty(value) ? null : value;
    }

    public String get(String path, Supplier<String> lambda) {
        String value = get(path);
        return value == null && lambda != null ? lambda.get() : value;
    }

    public static JsonElement<?> asJsonElement(Object input) {
        if (input instanceof Boolean) {
            return JsonBooleanValue.of((Boolean) input);
        } else if (input instanceof Number) {
            return JsonNumberValue.of((Number) input);
        } else if (input instanceof String) {
            return JsonStringValue.of((String) input);
        } else if (input instanceof Date) {
            return JsonDateValue.of((Date) input);
        } else if (input instanceof JsonNode && ((JsonNode) input).isArray()) {
            JsonArray jsonArray = JsonArray.create();
            ((JsonNode) input).getArray().forEach(item -> jsonArray.add(asJsonElement(item)));
            return jsonArray;
        } else if (input instanceof Iterable<?>) {
            JsonArray jsonArray = JsonArray.create();
            ((Iterable<?>) input).forEach(item -> jsonArray.add(asJsonElement(item)));
            return jsonArray;
        } else if (input instanceof JSONObject) {
            JsonObject jsonObject = JsonObject.create();
            JSONObject object = (JSONObject) input;
            for (String key : object.keySet())
                jsonObject.put(key, asJsonElement(object.get(key)));
            return jsonObject;
        } else if (input instanceof Map) {
            JsonObject jsonObject = JsonObject.create();
            ((Map<?, ?>) input).forEach((key, value) -> jsonObject.put(String.valueOf(key), asJsonElement(value)));
            return jsonObject;
        } else {
            if (input != null)
                Logging.logWarning(format("Unable to transform %s (%s)", input, input.getClass().getName()), Json.class);
            return JsonNullValue.NULL;
        }
    }
}
