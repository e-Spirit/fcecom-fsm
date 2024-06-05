package to.be.renamed.dap.content.aspects;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomContent;
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

public class EcomContentReporting implements Reporting, ReportItemsProviding<EcomContent>, StaticItemsProviding {

    private final BaseContext baseContext;
    private final ImageAgent imageAgent;
    private final EcomConnectScope scope;

    public EcomContentReporting(final BaseContext baseContext, final EcomConnectScope scope) {
        this.baseContext = baseContext;
        this.imageAgent = baseContext.requestSpecialist(ImageAgent.TYPE);
        this.scope = scope;
    }

    @Override
    public Image<?> getReportIcon(final boolean active) {
        if (baseContext.is(BaseContext.Env.WEBEDIT)) {
            return imageAgent.getImageFromUrl(EcomConnectWebApp.CONTENT_DAP_ICON);
        } else {
            return imageAgent.getImageFromIcon(new ImageIcon(getClass().getResource("/files-web/fcecom-content.png")));
        }
    }

    @Override
    public ReportItem<EcomContent> getClickItem() {
        if (baseContext.is(BaseContext.Env.WEBEDIT)) {
            return new EcomWebeditExecutableReportItem<>(scope);
        }
        return null;
    }

    @Override
    public @NotNull Collection<? extends ReportItem<EcomContent>> getItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends Item<BaseContext>> getStaticItems() {
        return Collections.singletonList(new EcomContentStaticReportItem(scope));
    }
}
