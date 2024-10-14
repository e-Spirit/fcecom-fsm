package to.be.renamed.bridge;

import to.be.renamed.bridge.client.Json;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.Serializable;

import static java.lang.String.format;

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

    public EcomProduct(String id, String type, String lang, String pageRefUid, String label, String extract, String thumbnail, String image,
                       String categoryId) {
        super(id, type, lang, pageRefUid, label);
        this.extract = extract;
        this.thumbnail = thumbnail;
        this.image = image;
        this.categoryId = categoryId;
    }

    @Override
    public String getType() {
        return EcomId.PRODUCT_TEMPLATE_UID;
    }

    @Override
    public JsonObject getValue() {
        JsonObject jsonObject = new JsonObject();
        if (type != null) {
            jsonObject.add("type", new JsonPrimitive(type));
        }
        if (lang != null) {
            jsonObject.add("lang", new JsonPrimitive(lang));
        }
        if (id != null) {
            jsonObject.add("id", new JsonPrimitive(id));
        }
        if (label != null) {
            jsonObject.add("label", new JsonPrimitive(label));
        }
        if (extract != null) {
            jsonObject.add("extract", new JsonPrimitive(extract));
        }
        if (thumbnail != null) {
            jsonObject.add("thumbnail", new JsonPrimitive(thumbnail));
        }
        if (image != null) {
            jsonObject.add("image", new JsonPrimitive(image));
        }
        if (categoryId != null) {
            jsonObject.add("categoryId", new JsonPrimitive(categoryId));
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

    @Override
    public String toString() {
        return format(
            "EcomProduct: {id: %s, type: %s, lang: %s, pageRefUid: %s, label: %s, extract: %s, thumbnail: %s, image: %s, categoryId: %s}",
            id,
            type,
            lang,
            pageRefUid,
            label,
            extract,
            thumbnail,
            image,
            categoryId
        );
    }
}
