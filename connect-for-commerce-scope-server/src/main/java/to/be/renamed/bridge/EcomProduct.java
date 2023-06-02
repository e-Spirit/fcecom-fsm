package to.be.renamed.bridge;

import to.be.renamed.bridge.client.Json;
import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.JsonPair;
import de.espirit.firstspirit.json.values.JsonStringValue;

import java.io.Serializable;

/**
 * Object representation of a product page from the shop system.
 */
public class EcomProduct extends EcomId implements Serializable {

    private static final long serialVersionUID = -3440220543137621626L;
    private final String extract;
    private final String thumbnail;
    private final String image;
    private final String categoryId;

    public EcomProduct(Json json) {
        super(json);
        extract = json.get("extract");
        thumbnail = json.get("thumbnail");
        image = json.get("image");
        categoryId = json.get("categoryId");
    }


    @Override
    public String getType() {
        return EcomId.PRODUCT_TEMPLATE_UID;
    }

    @Override
    public JsonObject getValue() {
        JsonObject jsonObject = JsonObject.create();
        if (type != null) {
            jsonObject.put(JsonPair.of("type", JsonStringValue.of(type)));
        }
        if (lang != null) {
            jsonObject.put(JsonPair.of("lang", JsonStringValue.of(lang)));
        }
        if (id != null) {
            jsonObject.put(JsonPair.of("id", JsonStringValue.of(id)));
        }
        if (label != null) {
            jsonObject.put(JsonPair.of("label", JsonStringValue.of(label)));
        }
        if (extract != null) {
            jsonObject.put(JsonPair.of("extract", JsonStringValue.of(extract)));
        }
        if (thumbnail != null) {
            jsonObject.put(JsonPair.of("thumbnail", JsonStringValue.of(thumbnail)));
        }
        if (image != null) {
            jsonObject.put(JsonPair.of("image", JsonStringValue.of(image)));
        }
        if (categoryId != null) {
            jsonObject.put(JsonPair.of("categoryId", JsonStringValue.of(categoryId)));
        }

        return jsonObject;
    }

    public String getExtract() {
        return extract;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getImage() {
        return image;
    }

}
