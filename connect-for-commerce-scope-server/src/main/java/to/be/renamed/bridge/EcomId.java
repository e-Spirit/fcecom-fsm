package to.be.renamed.bridge;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.OrphanedPageRefException;
import to.be.renamed.bridge.client.Json;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.access.store.sitestore.PageRef;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import static java.lang.String.format;

/**
 * Object representation of a general page from the shop system.
 */
public abstract class EcomId implements Serializable {

    public static final String TYPE_CATEGORY = "category";
    public static final String TYPE_PRODUCT = "product";
    public static final String TYPE_CONTENT = "content";
    public static final String TYPE_CONTENT_DEPRECATED = "contentpages";
    private static final long serialVersionUID = -4130798586510507783L;

    protected final String type;
    protected final String lang;
    protected String id;
    protected final String pageRefUid;
    protected final String label;
    protected boolean isManaged;

    protected EcomId(Json json) {
        type = json.get("type");
        lang = json.get("lang");
        id = json.get("id");
        pageRefUid = json.get("pageRefUid");
        label = json.get("label");
    }

    protected EcomId(String id, String type, String lang, String pageRefUid, String label) {
        this.id = id;
        this.type = type;
        this.lang = lang;
        this.pageRefUid = pageRefUid;
        this.label = label;
    }

    public static EcomId from(Json json) {
        if (json != null) {
            String type = json.get("type");
            if (type != null) {
                return switch (type) {
                    case TYPE_CATEGORY -> new EcomCategory(json);
                    case TYPE_PRODUCT -> new EcomProduct(json);
                    default -> new EcomContent(json);
                };
            } else {
                Logging.logWarning("Could not resolve EcomId-JSON " + json, EcomId.class);
            }
        }
        return null;
    }

    /**
     * Creates an EcomId from values.
     *
     * @param type       The type of the EcomId
     * @param id         The id from the shop system
     * @param lang       The current language
     * @param pageRefUid The Uid of the pageRef
     * @return EcomId internal Object to describe Product, Category or Content
     */
    public static EcomId from(String type, String id, String lang, String pageRefUid) {
        if (type != null) {
            return switch (type) {
                case TYPE_CATEGORY -> new EcomCategory(id, type, lang, pageRefUid, null);
                case TYPE_PRODUCT -> new EcomProduct(id, type, lang, pageRefUid, null, null, null, null, null);
                default -> new EcomContent(id, type, lang, pageRefUid, null, null);
            };
        } else {
            Logging.logWarning("Could not resolve Page because of missing type.", EcomId.class);
        }
        return null;
    }

    /**
     * Creates an EcomId from a page ref
     *
     * @param pageRef  The page ref to create an EcomId from
     * @param language Current language
     * @param scope    The EcomConnectScope
     * @return EcomId internal Object to describe Product, Category or Content
     * @throws OrphanedPageRefException if the page ref has no page, we assume it is orphaned
     */
    public static EcomId from(PageRef pageRef, Language language, final EcomConnectScope scope) {
        if (pageRef != null && language != null) {
            Page page = pageRef.getPage();
            if (page == null) {
                throw new OrphanedPageRefException(format(
                    "Could not find page for page ref with%n\tpageId: %s", pageRef.getPageId()));
            }

            String pageId = EcomIdUtilities.getPageId(page, language, scope);

            String type = page.getTemplate().getUid();
            String lang = language.getAbbreviation();
            return from(type, pageId, lang, pageRef.getUid());
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

    public Boolean isManaged() {
        return isManaged;
    }

    public void setManaged(boolean isManaged) {
        this.isManaged = isManaged;
    }

    public EcomElement getElement(EcomConnectScope scope) {
        return new EcomElement(scope, this);
    }

    public void setId(String id) {
        this.id = id;
    }

    public @Nullable JsonObject getValue() {
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

        return jsonObject;
    }
}
