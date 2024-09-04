package to.be.renamed.dap.product;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomProduct;
import to.be.renamed.dap.product.aspects.EcomProductReporting;
import to.be.renamed.module.ProjectAppHelper;
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

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Products Data Access Plugin", displayName = ProjectAppHelper.MODULE_NAME
                                                                                                       + " - Data Access Plugin: Products")
public class EcomProductDataAccessPlugin implements DataAccessPlugin<EcomProduct> {

    private static final Class<?> logger = EcomProductDataAccessPlugin.class;
    private DataAccessAspectMap dataAccessAspects;
    private EcomConnectScope scope;

    @Override
    public <A> A getAspect(final @NotNull DataAccessAspectType<A> type) {
        return this.dataAccessAspects.get(type);
    }

    @Override
    public @NotNull String getLabel() {
        return scope.getLabel(scope.getDisplayLanguage(), "report.products.label");
    }

    @Override
    public Image<?> getIcon() {
        return null;
    }

    @Override
    public @NotNull DataAccessSessionBuilder<EcomProduct> createSessionBuilder() {
        Logging.logDebug("Creating new SessionBuilder", logger);
        return new EcomProductDataAccessSessionBuilder(scope);
    }

    @Override
    public void setUp(final @NotNull BaseContext context) {
        dataAccessAspects = new DataAccessAspectMap();
        scope = new EcomConnectScope(context);
        final long projectId = context.requireSpecialist(ProjectAgent.TYPE).getId();

        if (ProjectAppHelper.isInstalled(context, projectId)) {
            final EcomProductReporting productReporting = new EcomProductReporting(context, scope);
            dataAccessAspects.put(Reporting.TYPE, productReporting);
            dataAccessAspects.put(ReportItemsProviding.TYPE, productReporting);
            dataAccessAspects.put(StaticItemsProviding.TYPE, productReporting);
        }
        Logging.logDebug("DAP has been set up", logger);
    }

    @Override
    public void tearDown() {
        // not needed
    }
}
