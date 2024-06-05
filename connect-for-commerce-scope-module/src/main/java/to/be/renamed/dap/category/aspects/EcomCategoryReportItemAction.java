package to.be.renamed.dap.category.aspects;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.product.EcomProductDataAccessPlugin;
import to.be.renamed.module.EcomConnectWebApp;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.report.JavaClientExecutableReportItem;
import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.webedit.plugin.report.WebeditExecutableReportItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

import javax.swing.Icon;

public class EcomCategoryReportItemAction implements WebeditExecutableReportItem<EcomCategory>, JavaClientExecutableReportItem<EcomCategory> {

    private final EcomConnectScope scope;

    public EcomCategoryReportItemAction(final EcomConnectScope scope) {
        super();
        this.scope = scope;
    }

    @Override
    public boolean isVisible(ReportContext<EcomCategory> context) {
        return context.is(BaseContext.Env.WEBEDIT);
    }

    @Override
    public boolean isEnabled(@NotNull final ReportContext<EcomCategory> reportContext) {
        return true;
    }

    @Override
    public String getIconPath(@NotNull ReportContext<EcomCategory> context) {
        return EcomConnectWebApp.PRODUCT_DAP_ICON;
    }

    @Override
    public @NotNull String getLabel(@NotNull ReportContext<EcomCategory> context) {
        return scope.getLabel("report.categories.action.show-products");
    }

    @Override
    public void execute(@NotNull ReportContext<EcomCategory> context) {
        Map<String, Object> params = Collections.singletonMap(EcomDapUtilities.FILTER_CATEGORY, context.getObject().getId());
        scope.openReport(context, EcomProductDataAccessPlugin.class, params);
    }

    @Override
    public @Nullable Icon getIcon(@NotNull final ReportContext<EcomCategory> ecomCategoryReportContext) {
        return null;
    }
}
