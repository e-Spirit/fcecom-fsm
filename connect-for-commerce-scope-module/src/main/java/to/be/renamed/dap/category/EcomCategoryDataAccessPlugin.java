package to.be.renamed.dap.category;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.dap.category.aspects.EcomCategoryReporting;
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

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Categories Data Access Plugin", displayName = ProjectAppHelper.MODULE_NAME
                                                                                                         + " - Data Access Plugin: Categories")
public class EcomCategoryDataAccessPlugin implements DataAccessPlugin<EcomCategory> {
    private static final Class<?> logger = EcomCategoryDataAccessPlugin.class;
    private DataAccessAspectMap dataAccessAspects;
    private EcomConnectScope scope;

    @Override
    public <A> A getAspect(final @NotNull DataAccessAspectType<A> type) {
        return this.dataAccessAspects.get(type);
    }

    @Override
    public @NotNull String getLabel() {
        return scope.getLabel("report.categories.label");
    }

    @Override
    public Image<?> getIcon() {
        return null;
    }

    @Override
    public @NotNull DataAccessSessionBuilder<EcomCategory> createSessionBuilder() {
        Logging.logDebug("Creating new SessionBuilder", logger);
        return new EcomCategoryDataAccessSessionBuilder(scope);
    }

    @Override
    public void setUp(final @NotNull BaseContext context) {
        dataAccessAspects = new DataAccessAspectMap();
        scope = new EcomConnectScope(context);
        final long projectId = context.requireSpecialist(ProjectAgent.TYPE).getId();

        if (ProjectAppHelper.isInstalled(context, projectId)) {
            final EcomCategoryReporting categoryReporting = new EcomCategoryReporting(context, scope);
            dataAccessAspects.put(Reporting.TYPE, categoryReporting);
            dataAccessAspects.put(ReportItemsProviding.TYPE, categoryReporting);
            dataAccessAspects.put(StaticItemsProviding.TYPE, categoryReporting);
        }
        Logging.logDebug("DAP has been set up", logger);
    }

    @Override
    public void tearDown() {
        // not needed
    }
}
