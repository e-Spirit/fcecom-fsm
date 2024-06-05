package to.be.renamed.dap.product.aspects;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomProduct;
import to.be.renamed.dap.EcomWebeditExecutableReportItem;
import to.be.renamed.module.EcomConnectWebApp;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.Item;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ReportItemsProviding;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Reporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StaticItemsProviding;
import de.espirit.firstspirit.client.plugin.report.ReportItem;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

import javax.swing.ImageIcon;

public class EcomProductReporting implements Reporting, ReportItemsProviding<EcomProduct>, StaticItemsProviding {

    private final BaseContext baseContext;
    private final ImageAgent imageAgent;
    private final EcomConnectScope scope;

    public EcomProductReporting(final BaseContext baseContext, final EcomConnectScope scope) {
        this.baseContext = baseContext;
        this.imageAgent = baseContext.requestSpecialist(ImageAgent.TYPE);
        this.scope = scope;
    }

    @Override
    public Image<?> getReportIcon(final boolean active) {
        if (baseContext.is(BaseContext.Env.WEBEDIT)) {
            return imageAgent.getImageFromUrl(EcomConnectWebApp.PRODUCT_DAP_ICON);
        } else {
            return imageAgent.getImageFromIcon(new ImageIcon(getClass().getResource("/files-web/fcecom-product.png")));
        }
    }

    @Override
    public ReportItem<EcomProduct> getClickItem() {
        if (baseContext.is(BaseContext.Env.WEBEDIT)) {
            return new EcomWebeditExecutableReportItem<>(scope);
        }
        return null;
    }

    @Override
    public @NotNull Collection<? extends ReportItem<EcomProduct>> getItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Item<BaseContext>> getStaticItems() {
        return Collections.singletonList(new EcomProductStaticReportItem(scope));
    }
}
