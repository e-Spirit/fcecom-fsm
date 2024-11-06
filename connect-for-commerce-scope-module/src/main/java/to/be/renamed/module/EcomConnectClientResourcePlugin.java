package to.be.renamed.module;

import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.agency.ModuleAdminAgent;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.webedit.plugin.ClientResourcePlugin;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Client Resource Plugin", displayName = ProjectAppHelper.MODULE_NAME
                                                                                                  + " - Client Resource Plugin")
public class EcomConnectClientResourcePlugin implements ClientResourcePlugin {

    private String loaderPath;

    @Override
    public @NotNull List<String> getScriptUrls() {
        if (loaderPath != null) {
            return Collections.singletonList(loaderPath);
        }
        return Collections.emptyList();
    }

    @Override
    public @NotNull List<String> getStylesheetUrls() {
        return Collections.emptyList();
    }

    @Override
    public void setUp(@NotNull BaseContext context) {
        if (isProjectAppInstalled(context)) {
            final var projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(context);
            final var projectAppConfiguration = projectAppConfigurationService.loadConfiguration();

            parseConfiguration(projectAppConfiguration);
        }
    }

    protected boolean isProjectAppInstalled(final @NotNull BaseContext context) {
        ModuleAdminAgent moduleAdminAgent = context.requireSpecialist(ModuleAdminAgent.TYPE);
        ProjectAgent projectAgent = context.requireSpecialist(ProjectAgent.TYPE);
        final long projectId = projectAgent.getId();
        final Collection<Project> projectAppUsages = moduleAdminAgent.getProjectAppUsages(ProjectAppHelper.MODULE_NAME, ProjectAppHelper.PROJECT_APP_NAME);

        for (final Project project : projectAppUsages) {
            if (projectId == project.getId()) {
                Logging.logInfo("'" + ProjectAppHelper.MODULE_NAME + " - Client Resource Plugin' enabled for project " + projectId, getClass());
                return true;
            }
        }

        Logging.logInfo("'" + ProjectAppHelper.MODULE_NAME + " - Client Resource Plugin' disabled for project " + projectId, getClass());
        return false;
    }

    void parseConfiguration(final ProjectAppConfiguration projectAppConfiguration) {
        boolean useCCExtensions = projectAppConfiguration.getGeneralConfig().useCCExtensions();
        if (useCCExtensions) {
            loaderPath = projectAppConfiguration.getGeneralConfig().getCcExtensionsUrl();
        }
    }

    @Override
    public void tearDown() {
        // not needed
    }
}
