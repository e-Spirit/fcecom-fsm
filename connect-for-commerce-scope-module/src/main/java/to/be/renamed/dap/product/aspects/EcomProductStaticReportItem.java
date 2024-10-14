package to.be.renamed.dap.product.aspects;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.dap.product.EcomProductDataAccessPlugin;
import to.be.renamed.module.ServiceFactory;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.Item;
import de.espirit.firstspirit.client.plugin.JavaClientExecutablePluginItem;
import de.espirit.firstspirit.client.plugin.WebeditExecutablePluginItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EcomProductStaticReportItem
    implements Item<BaseContext>, WebeditExecutablePluginItem<BaseContext>, JavaClientExecutablePluginItem<BaseContext> {

    private final EcomConnectScope scope;

    public EcomProductStaticReportItem(final EcomConnectScope scope) {
        this.scope = scope;
    }

    @Override
    public @NotNull String getLabel(@NotNull BaseContext context) {
        return scope.getLabel(scope.getDisplayLanguage(), "report.products.static.refresh");
    }

    @Override
    public void execute(@NotNull BaseContext context) {
        ServiceFactory.getBridgeService(context).invalidateCache();
        scope.openReport(context, EcomProductDataAccessPlugin.class);
    }

    @Override
    public boolean isVisible(@NotNull final BaseContext baseContext) {
        return true;
    }

    @Override
    public boolean isEnabled(@NotNull final BaseContext baseContext) {
        return true;
    }

    @Override
    public @Nullable Icon getIcon(@NotNull final BaseContext baseContext) {
        return null;
    }

    @Override
    public @Nullable String getIconPath(@NotNull final BaseContext baseContext) {
        return null;
    }
}




