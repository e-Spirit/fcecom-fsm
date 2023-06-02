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
    BRIDGE_TEST_CONNECTION("projectApp.configuration.actions.bridge-test.button.label"),
    REPORT_CATEGORY_LEVELS("projectApp.configuration.items.category-levels.label"),
    REPORT_PRODUCT_LEVELS("projectApp.configuration.items.product-levels.label"),
    GENERAL_TAB_TITLE("projectApp.configuration.sections.general.label"),
    BRIDGE_TAB_TITLE("projectApp.configuration.sections.bridge.label"),
    REPORT_TAB_TITLE("projectApp.configuration.sections.report.label");

    private final String resourceBundleKey;

    Label(final String resourceBundleKey) {
        this.resourceBundleKey = resourceBundleKey;
    }

    public String getResourceBundleKey() {
        return resourceBundleKey;
    }
}
