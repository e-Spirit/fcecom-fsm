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
import de.espirit.firstspirit.forms.NoSuchFormFieldException;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import static java.lang.String.format;

/**
 * Object representation of a general page from the shop system.
 */
public abstract class EcomId implements Serializable {

    protected static final String CATEGORY_TEMPLATE_UID = "category";
    protected static final String PRODUCT_TEMPLATE_UID = "product";
    protected static final String CONTENT_TEMPLATE_UID = "contentpages";
    public static final String PAGE_ID_FORM_FIELD = "id";
    public static final String PAGE_TYPE_FORM_FIELD = "type";
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
                switch (type) {
                    case CATEGORY_TEMPLATE_UID:
                        return new EcomCategory(json);
                    case PRODUCT_TEMPLATE_UID:
                        return new EcomProduct(json);
                    default:
                        return new EcomContent(json);
                }
            } else {
                Logging.logWarning("Could not resolve EcomId-JSON " + json.toString(), EcomId.class);
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
            switch (type) {
                case CATEGORY_TEMPLATE_UID:
                    return new EcomCategory(id, type, lang, pageRefUid, null);
                case PRODUCT_TEMPLATE_UID:
                    return new EcomProduct(id, type, lang, pageRefUid, null, null, null, null, null);
                default:
                    return new EcomContent(id, type, lang, pageRefUid, null, null);
            }
        } else {
            Logging.logWarning("Could not resolve Page because of missing type.", EcomId.class);
        }
        return null;
    }

    /**
     * Checks if the current page contains the page id form field
     *
     * @param page the page to check for the page id form field
     * @return true if page contains page id form field, false if it's missing
     */
    public static boolean hasPageIdField(Page page) {
        try {
            page.getFormData().get(null, PAGE_ID_FORM_FIELD);
            return true;
        } catch (NoSuchFormFieldException e) {
            Logging.logDebug("The id form field is missing on the page with the UID: " + page.getUid(), e, EcomId.class);
            return false;
        }
    }

    /**
     * Checks if the current page contains the page type form field
     *
     * @param page the page to check for the page type form field
     * @return true if page contains page type form field, false if it's missing
     */
    public static boolean hasPageTypeField(Page page) {
        try {
            page.getFormData().get(null, PAGE_TYPE_FORM_FIELD);
            return true;
        } catch (NoSuchFormFieldException e) {
            Logging.logDebug("The type form field is missing on the page with the UID: " + page.getUid(), e, EcomId.class);
            return false;
        }
    }

    /**
     * Retrieves the page id from the given page for the specified language
     *
     * @param page     the page to retrieve the page id from
     * @param language the language for which the page id is retrieved
     * @return the page id as a string or null if the page id is missing or invalid
     */
    public static String getPageId(Page page, Language language) {
        if (!hasPageIdField(page)) {
            return null;
        }

        Object pageId = page.getFormData().get(language, PAGE_ID_FORM_FIELD).get();
        if (pageId instanceof String id && !id.isEmpty()) {
            return id;
        }
        return null;
    }

    /**
     * Retrieves the page type from the given page for the specified language
     *
     * @param page     the page to retrieve the page type from
     * @param language the language for which the page type is retrieved
     * @return the page type as a string or null if the page type is missing or invalid
     */
    public static String getPageType(Page page, Language language) {
        if (!hasPageTypeField(page)) {
            return null;
        }

        Object pageType = page.getFormData().get(language, PAGE_TYPE_FORM_FIELD).get();
        if (pageType instanceof String type && !type.isEmpty()) {
            return type;
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

            String pageId = getPageId(page, language);

            String type = page.getTemplate().getUid();
            String lang = EcomConnectScope.getLang(language);
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
