package to.be.renamed.dap.content;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.EcomContent;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.content.aspects.EcomContentReporting;
import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessPlugin;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ReportItemsProviding;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Reporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StaticItemsProviding;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - ContentPages Data Access Plugin", displayName = ProjectAppHelper.MODULE_NAME
                                                                                                           + " - Data Access Plugin: ContentPages")
public class EcomContentDataAccessPlugin implements DataAccessPlugin<EcomContent> {

    private static final Class<?> logger = EcomContentDataAccessPlugin.class;
    private DataAccessAspectMap dataAccessAspects;
    private EcomConnectScope scope;

    @Override
    public <A> A getAspect(final @NotNull DataAccessAspectType<A> type) {
        return this.dataAccessAspects.get(type);
    }

    @Override
    public @NotNull String getLabel() {
        return scope.getLabel(scope.getDisplayLanguage(), "report.contents.label");
    }

    @Override
    public Image<?> getIcon() {
        return null;
    }

    @Override
    public @NotNull DataAccessSessionBuilder<EcomContent> createSessionBuilder() {
        Logging.logDebug("Creating new SessionBuilder", logger);
        return new EcomContentDataAccessSessionBuilder(scope);
    }

    @Override
    public void setUp(final @NotNull BaseContext context) {
        dataAccessAspects = new DataAccessAspectMap();
        scope = EcomConnectScope.create(context);
        final long projectId = context.requireSpecialist(ProjectAgent.TYPE).getId();

        if (ProjectAppHelper.isInstalled(context, projectId) && isAvailable()) {
            final EcomContentReporting contentReporting = new EcomContentReporting(context, scope);
            dataAccessAspects.put(Reporting.TYPE, contentReporting);
            dataAccessAspects.put(ReportItemsProviding.TYPE, contentReporting);
            dataAccessAspects.put(StaticItemsProviding.TYPE, contentReporting);
        }
        Logging.logDebug("DAP has been set up", logger);
    }

    @Override
    public void tearDown() {
        // not needed
    }

    public boolean isAvailable() {
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());
        try {
            return bridgeService.hasContent();
        } catch (BridgeConnectionException e) {
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e,
                             this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
        }
        return false;
    }
}
