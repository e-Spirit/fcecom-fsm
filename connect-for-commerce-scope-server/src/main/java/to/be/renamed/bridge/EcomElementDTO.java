package to.be.renamed.bridge;


import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Data transfer object for transferring the data of the EcomElement to the bridge.
 * Comes with helper methods to create json from the data.
 */
public class EcomElementDTO implements Serializable {
    private static final long serialVersionUID = -5056032127227374769L;
    private final String template;
    private final boolean released;
    private final String pageUid;
    private final Map<String, String> label;
    private final Map<String, String> path;

    /**
     * Creates an EcomElementDTO from the given values.
     * @param template The page templates uid.
     * @param released Boolean which marks if the page will be released in the shop system.
     * @param pageUid The FirstSpirit PageUid
     * @param label The label of the page as a map with lang abbreviation as key and label as value.
     * @param path The path of the page as a map with lang abbreviation as key and path as value.
     */
    public EcomElementDTO(String template, boolean released, String pageUid, Map<String, String> label, Map<String, String> path) {
        this.template = template;
        this.released = released;
        this.pageUid = pageUid;
        this.label = label;
        this.path = path;
    }

    /**
     * Creates a FirstSpirit JsonObject representation of the EcomElementDTO.
     * Used as request body for creating and updating a content page via the bridge.
     * @return FirstSpirit JsonObject representation of the EcomElementDTO
     */
    public JsonObject getJsonModel() {
        JsonObject jsonModel = new JsonObject();
        jsonModel.add("template", template != null ? new JsonPrimitive(template) : JsonNull.INSTANCE);
        jsonModel.add("released", new JsonPrimitive(released));
        jsonModel.add("pageUid", pageUid != null ? new JsonPrimitive(pageUid) : JsonNull.INSTANCE);
        jsonModel.add("label", label != null && !label.isEmpty() ? jsonObjectFromMap(label) : JsonNull.INSTANCE);
        jsonModel.add("path", path != null && !path.isEmpty() ? jsonObjectFromMap(path) : JsonNull.INSTANCE);

        return jsonModel;
    }

    /**
     * Gives the first value rom a map with strings as key and value if present.
     * @param input A map with strings as key and value.
     * @return The first value of the map or null if no value is present.
     */
    @Nullable
    private static String getFirstValue(Map<String, String> input) {
        if (input != null) {
            Map.Entry<String, String> first = input.entrySet().stream().findFirst().orElse(null);
            if (first != null) {
                return first.getValue();
            }
            return null;
        }
        return null;
    }

    /**
     * Creates a FirstSpirit JsonObject from a map with strings as key and value.
     * @param input A map with strings as key and value.
     * @return A FirstSpirit JsonObject with the keys and values from the map as keys and values.
     */
    private static JsonObject jsonObjectFromMap(Map<String, String> input) {
        JsonObject jsonModel = new JsonObject();
        input.forEach((key, value) -> jsonModel.add(key, new JsonPrimitive(value)));
        return jsonModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EcomElementDTO that = (EcomElementDTO) o;
        return released == that.released
               && template.equals(that.template)
               && pageUid.equals(that.pageUid)
               && Objects.equals(label, that.label)
               && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, released, pageUid, label, path);
    }
}
