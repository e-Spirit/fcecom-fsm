package to.be.renamed.dap.content;

public record EcomContentReportIcon(String webEditFile, String siteArchitectFile) {

    private static final String DAP_ICON_CC = "fcecom-content.svg";
    private static final String DAP_ICON_SA = "/files-web/fcecom-content.png";
    private static final String DAP_ICON_MANAGED_CC = "fcecom-content-managed.svg";
    private static final String DAP_ICON_MANAGED_SA = "/files-web/fcecom-content-managed.png";

    public static EcomContentReportIcon managed(boolean isManaged) {
        return isManaged ? managed() : standard();
    }

    public static EcomContentReportIcon standard() {
        return new EcomContentReportIcon(DAP_ICON_CC, DAP_ICON_SA);
    }

    public static EcomContentReportIcon managed() {
        return new EcomContentReportIcon(DAP_ICON_MANAGED_CC, DAP_ICON_MANAGED_SA);
    }
}