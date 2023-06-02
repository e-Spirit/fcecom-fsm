package to.be.renamed.bridge;

import to.be.renamed.bridge.client.Json;
import to.be.renamed.EcomConnectScope;
import to.be.renamed.OrphanedPageRefException;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.json.JsonElement;
import de.espirit.firstspirit.json.JsonObject;
import de.espirit.firstspirit.json.JsonPair;
import de.espirit.firstspirit.json.values.JsonNullValue;
import de.espirit.firstspirit.json.values.JsonStringValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import static java.lang.String.format;

/**
 * Object representation of a general page from the shop system.
 */
public abstract class EcomId implements JsonElement<JsonObject>, Serializable {

    protected static final String CATEGORY_TEMPLATE_UID = "category";
    protected static final String PRODUCT_TEMPLATE_UID = "product";
    protected static final String CONTENT_TEMPLATE_UID = "contentpages";
    protected static final String PAGE_ID_FORM_FIELD = "id";
    private static final long serialVersionUID = -4130798586510507783L;

    protected final String type;
    protected final String lang;
    protected String id;
    protected final String pageRefUid;
    protected final String label;

    protected EcomId(Json json) {
        type = json.get("type");
        lang = json.get("lang");
        id = json.get("id");
        pageRefUid = json.get("pageRefUid");
        label = json.get("label");
    }

    public static EcomId from(Json json) {
        if (json != null) {
            String type = json.get("type");
            if (type != null) {
                switch (type) {
                    case CATEGORY_TEMPLATE_UID:
                        return new EcomCategory(json);
                    case PRODUCT_TEMPLATE_UID:
                        return new EcomProduct(json);
                    default:
                        return new EcomContent(json);
                }
            } else {
                Logging.logWarning("Could not resolve EcomId-JSON " + json.json(), EcomId.class);
            }
        }
        return null;
    }

    public static String getPageId(Page page, Language language) {
        Object pageId = page.getFormData().get(language, PAGE_ID_FORM_FIELD).get();
        if (pageId instanceof String) {
            String id = (String) pageId;
            if (!id.isEmpty()) {
                return id;
            }
        }
        return null;
    }

    /**
     * Creates an EcomId from a page ref
     *
     * @param pageRef  the page ref to create an EcomId from
     * @param language current language
     * @return EcomId internal Object to describe Product, Category or Content
     * @throws OrphanedPageRefException if the page ref has no page, we assume it is orphaned
     */
    public static EcomId from(PageRef pageRef, Language language) {
        if (pageRef != null && language != null) {
            Page page = pageRef.getPage();
            if (page == null) {
                throw new OrphanedPageRefException(format(
                    "Could not find page for page ref with%n\tpageId: %s", pageRef.getPageId()));
            }
            Json json = new Json();
            String pageId = getPageId(page, language);
            json.getValue().put("type", JsonStringValue.of(page.getTemplate().getUid()));
            json.getValue().put("id", pageId == null ? JsonNullValue.NULL : JsonStringValue.of(pageId));
            json.getValue().put("lang", JsonStringValue.of(EcomConnectScope.getLang(language)));
            json.getValue().put("pageRefUid", JsonStringValue.of(pageRef.getUid()));
            return from(json);
        }
        return null;
    }

    public boolean isValid() {
        return hasId() && getType() != null;
    }

    public String getType() {
        return type;
    }

    public boolean hasId() {
        return getId() != null;
    }

    public String getId() {
        return id;
    }

    public boolean hasPageRefUid() {
        return getPageRefUid() != null;
    }

    public String getPageRefUid() {
        return pageRefUid;
    }

    public String getLang() {
        return lang;
    }

    public String getLabel() {
        return label;
    }

    public EcomElement getElement(EcomConnectScope scope) {
        return new EcomElement(scope, this);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public @Nullable JsonObject getValue() {
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

        return jsonObject;
    }

    @Override
    public void writeTo(@NotNull Writer writer) throws IOException {
        getValue().writeTo(writer);
    }
}
