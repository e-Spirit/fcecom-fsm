package to.be.renamed.dap.category;

public record EcomCategoryReportIcon(String webEditFile, String siteArchitectFile) {

    private static final String DAP_ICON_CC = "fcecom-category.svg";
    private static final String DAP_ICON_SA = "/files-web/fcecom-category.png";
    private static final String DAP_ICON_MANAGED_CC = "fcecom-category-managed.svg";
    private static final String DAP_ICON_MANAGED_SA = "/files-web/fcecom-category-managed.png";

    public static EcomCategoryReportIcon managed(boolean isManaged) {
        return isManaged ? managed() : standard();
    }

    public static EcomCategoryReportIcon standard() {
        return new EcomCategoryReportIcon(DAP_ICON_CC, DAP_ICON_SA);
    }

    public static EcomCategoryReportIcon managed() {
        return new EcomCategoryReportIcon(DAP_ICON_MANAGED_CC, DAP_ICON_MANAGED_SA);
    }
}