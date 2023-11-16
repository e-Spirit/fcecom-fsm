package to.be.renamed.module;

import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.webedit.plugin.ClientResourcePlugin;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Client Resource Plugin", displayName = ProjectAppHelper.MODULE_NAME + " - Client Resource Plugin")
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
        final var projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(context);
        final var projectAppConfiguration = projectAppConfigurationService.loadConfiguration();

        parseConfiguration(projectAppConfiguration);
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
