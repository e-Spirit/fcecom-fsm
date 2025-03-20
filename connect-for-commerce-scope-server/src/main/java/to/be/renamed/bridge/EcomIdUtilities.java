package to.be.renamed.bridge;

import to.be.renamed.EcomConnectScope;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.forms.NoSuchFormFieldException;

public class EcomIdUtilities {

    /**
     * Checks if the current page contains the page id form field
     *
     * @param page  the page to check for the page id form field
     * @param scope the current project scope of the requested page
     * @return true if page contains page id form field, false if it's missing
     */
    public static boolean hasPageIdField(Page page, final EcomConnectScope scope) {
        return hasFormField(page, scope.getIdField());
    }

    /**
     * Checks if the current page has the page type form field
     *
     * @param page  the page to check for the page type form field
     * @param scope the current project scope of the requested page
     * @return true if page contains page type form field, false if it's missing
     */
    public static boolean hasPageTypeField(Page page, final EcomConnectScope scope) {
        return hasFormField(page, scope.getPageTypeField());
    }

    /**
     * Checks if the current page has the specified field
     *
     * @param page      the page to check for the page type form field
     * @param fieldName the current project scope of the requested page
     * @return true if page contains specified form field, false if it's missing
     */
    public static boolean hasFormField(Page page, String fieldName) {
        try {
            page.getFormData().get(null, fieldName);
            return true;
        } catch (NoSuchFormFieldException e) {
            Logging.logDebug("The %s form field is missing on the page with the UID: %s".formatted(fieldName, page.getUid()), e, EcomId.class);
            return false;
        }
    }

    /**
     * Retrieves the page id from the given page for the specified language
     *
     * @param page     the page to retrieve the page id from
     * @param language the language for which the page id is retrieved
     * @param scope    the current project scope of the requested page
     * @return the page id as a string or null if the page id is missing or invalid
     */
    public static String getPageId(Page page, Language language, final EcomConnectScope scope) {
        if (!hasPageIdField(page, scope)) {
            return null;
        }

        Object pageId = page.getFormData().get(language, scope.getIdField()).get();
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
     * @param scope    the current project scope of the requested page
     * @return the page type as a string or null if the page type is missing or invalid
     */
    public static String getPageType(Page page, Language language, final EcomConnectScope scope) {
        if (!hasPageTypeField(page, scope)) {
            return null;
        }

        Object pageType = page.getFormData().get(language, scope.getPageTypeField()).get();
        if (pageType instanceof String type && !type.isEmpty()) {
            return type;
        }
        return null;
    }
}
