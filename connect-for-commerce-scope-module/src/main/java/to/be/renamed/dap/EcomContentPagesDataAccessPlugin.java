package to.be.renamed.dap;

import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.module.EcomConnectWebApp;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.EcomContent;
import to.be.renamed.module.ServiceFactory;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.client.plugin.report.ReportContext;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

import static java.lang.String.format;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - ContentPages Data Access Plugin", displayName = ProjectAppHelper.MODULE_NAME
                                                                                                           + " - Data Access Plugin: ContentPages")
public class EcomContentPagesDataAccessPlugin extends EcomAbstract<EcomContent> {

    public static final String FILTER_QUERY = "q";

    @Override
    public Class<EcomContent> getType() {
        return EcomContent.class;
    }

    @Override
    public boolean isAvailable() {
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());
        try {
            return bridgeService.hasContent();
        } catch (BridgeConnectionException e) {
            Logging.logError(format(ERROR_LOG_MESSAGE, ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            openDialog(e.getLocalizedMessage(), e.getErrorCode());
        }
        return false;
    }

    @Override
    public String getReportSvgIconPath() {
        return EcomConnectWebApp.CONTENT_DAP_ICON;
    }

    @Override
    public ImageIcon getReportPngImageIcon() {
        return new ImageIcon(getClass().getResource("/files-web/fcecom-content.png"));
    }

    @Override
    public String getReportLabel() {
        return scope.getLabel("report.contents.label");
    }

    @Override
    public Collection<EcomDataAccessPlugin.StaticReportItem> getStaticReportItems() {
        return Collections.singletonList(new EcomDataAccessPlugin.StaticReportItem() {

            @Override
            public @NotNull String getLabel(@NotNull BaseContext context) {
                return scope.getLabel("report.contents.static.refresh");
            }

            @Override
            public void execute(@NotNull BaseContext context) {
                ServiceFactory.getBridgeService(context).invalidateCache();
                scope.openReport(context, EcomContentPagesDataAccessPlugin.class);
            }
        });
    }

    @Override
    public void getReportFilter(EcomFilterBuilder filters) {
        filters.addTextField(FILTER_QUERY, scope.getLabel("report.contents.filter.q"));
    }

    @Override
    public List<EcomContent> resolve(Collection<String> identifiers) {
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());

        try {
            return bridgeService.getContent(identifiers, scope.getLang());
        } catch (BridgeConnectionException e) {
            Logging.logError(format(ERROR_LOG_MESSAGE, ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            openDialog(e.getLocalizedMessage(), e.getErrorCode());
            return Collections.emptyList();
        }
    }

    @Override
    public Iterator<EcomContent> getItems(Map<String, String> filters) {
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());

        try {
            return bridgeService.findContent(filters.get(FILTER_QUERY), scope.getLang()).iterator();
        } catch (BridgeConnectionException e) {
            Logging.logError(format(ERROR_LOG_MESSAGE, ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            openDialog(e.getLocalizedMessage(), e.getErrorCode());
            return Collections.emptyIterator();
        }
    }

    @Override
    public String getItemId(EcomContent item) {
        return item.getId();
    }

    @Override
    public String getItemHeader(EcomContent item, Language language) {
        return item.getLabel();
    }

    @Override
    public String getItemExtract(EcomContent item, Language language) {
        return item.getExtract();
    }

    @Override
    public void onItemClick(ReportContext<EcomContent> context) {
        scope.openStoreFrontUrl(context, context.getObject());
    }
}
