package to.be.renamed.dap.product;

public record EcomProductReportIcon(String webEditFile, String siteArchitectFile) {

    private static final String DAP_ICON_CC = "fcecom-product.svg";
    private static final String DAP_ICON_SA = "/files-web/fcecom-product.png";
    private static final String DAP_ICON_MANAGED_CC = "fcecom-product-managed.svg";
    private static final String DAP_ICON_MANAGED_SA = "/files-web/fcecom-product-managed.png";

    public static EcomProductReportIcon managed(boolean isManaged) {
        return isManaged ? managed() : standard();
    }

    public static EcomProductReportIcon standard() {
        return new EcomProductReportIcon(DAP_ICON_CC, DAP_ICON_SA);
    }

    public static EcomProductReportIcon managed() {
        return new EcomProductReportIcon(DAP_ICON_MANAGED_CC, DAP_ICON_MANAGED_SA);
    }
}