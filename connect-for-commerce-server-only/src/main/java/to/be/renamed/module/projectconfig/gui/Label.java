package to.be.renamed.module.projectconfig.gui;

/**
 * Stores the gui resource bundle keys
 */
public enum Label {
    CONTENT_CREATOR_EXTENSION("projectApp.configuration.items.content-creator-extension-url.label"),
    USE_CONTENT_CREATOR_EXTENSION("projectApp.configuration.items.use-content-creator-extension.label"),
    DISABLE_BRIDGE_PAGE_CREATION("projectApp.configuration.items.disable-bridge-page-creation.label"),
    BRIDGE_API_URL("projectApp.configuration.items.bridge-api-url.label"),
    BRIDGE_USERNAME("projectApp.configuration.items.bridge-username.label"),
    BRIDGE_PASSWORD("projectApp.configuration.items.bridge-password.label"),
    TEST_CONNECTION("projectApp.configuration.items.bridge-test.dialog.title"),
    BRIDGE_TEST_CONNECTION("projectApp.configuration.actions.bridge-test.button.label"),
    GENERATE_JWT_SECRET("projectApp.configuration.actions.jwt.secret.generate.button.label"),
    COPY_JWT_SECRET("projectApp.configuration.actions.jwt.secret.copy.button.label"),
    SHOW_PASSWORD("projectApp.configuration.actions.jwt.secret.show.button.label"),
    HIDE_PASSWORD("projectApp.configuration.actions.jwt.secret.hide.button.label"),
    REPORT_CATEGORY_LEVELS("projectApp.configuration.items.category-levels.label"),
    REPORT_PRODUCT_LEVELS("projectApp.configuration.items.product-levels.label"),
    JWT_SECRET("projectApp.configuration.items.jwt.secret.label"),
    CACHE_SIZE("projectApp.configuration.items.cache-size.label"),
    CACHE_AGE("projectApp.configuration.items.cache-age.label"),
    GENERAL_TAB_TITLE("projectApp.configuration.sections.general.label"),
    BRIDGE_TAB_TITLE("projectApp.configuration.sections.bridge.label"),
    REPORT_TAB_TITLE("projectApp.configuration.sections.report.label"),
    CACHE_TAB_TITLE("projectApp.configuration.sections.cache.label"),
    JWT_TAB_TITLE("projectApp.configuration.sections.jwt.label");

    private final String resourceBundleKey;

    Label(final String resourceBundleKey) {
        this.resourceBundleKey = resourceBundleKey;
    }

    public String getResourceBundleKey() {
        return resourceBundleKey;
    }
}
