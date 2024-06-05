package to.be.renamed.dap;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomId;

import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.webedit.plugin.report.WebeditExecutableReportItem;

import org.jetbrains.annotations.NotNull;

public class EcomWebeditExecutableReportItem<T> implements WebeditExecutableReportItem<T> {

    private final EcomConnectScope scope;

    public EcomWebeditExecutableReportItem(final EcomConnectScope scope) {
        this.scope = scope;
    }

    @Override
    public boolean isVisible(@NotNull ReportContext<T> context) {
        return true;
    }

    @Override
    public boolean isEnabled(@NotNull ReportContext<T> context) {
        return true;
    }

    @Override
    public @NotNull String getLabel(@NotNull ReportContext<T> context) {
        return "";
    }

    @Override
    public void execute(@NotNull ReportContext<T> context) {
        onItemClick(context);
    }

    @Override
    public String getIconPath(@NotNull ReportContext<T> context) {
        return null;
    }

    public void onItemClick(ReportContext<T> context) {
        scope.openStoreFrontUrl(context, (EcomId) context.getObject());
    }
}
