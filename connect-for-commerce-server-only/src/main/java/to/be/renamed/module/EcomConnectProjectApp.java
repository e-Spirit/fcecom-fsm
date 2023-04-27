package to.be.renamed.module;

import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.module.projectconfig.model.BridgeConfig;
import to.be.renamed.module.projectconfig.model.GeneralConfig;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import to.be.renamed.module.projectconfig.model.ReportConfig;
import com.espirit.moddev.components.annotations.ProjectAppComponent;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.io.FileHandle;
import de.espirit.firstspirit.io.FileSystem;
import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

import java.io.IOException;
import java.util.Properties;

@ProjectAppComponent(
    name = ProjectAppHelper.PROJECT_APP_NAME,
    displayName = ProjectAppHelper.PROJECT_APP_NAME,
    configurable = EcomConnectProjectConfig.class
)
public class EcomConnectProjectApp implements ProjectApp {

    private static final String CONFIG_BREAKING_VERSION = "2.5.0";

    private ProjectEnvironment environment;

    @Override
    public void init(ProjectAppDescriptor descriptor, ProjectEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void installed() {
        // Not used
    }

    @Override
    public void uninstalling() {
        // Not used
    }

    @Override
    public void updated(String oldVersionString) {
        Logging.logInfo("Updating project app for project with ID " + environment.getProjectId() + " from version " + oldVersionString, getClass());
        if (isConfigMigrationNeeded(oldVersionString)) {
            Logging.logInfo("Starting migration of project app configuration for project with ID " + environment.getProjectId(), getClass());
            final FileSystem<? extends FileHandle> fs = environment.getConfDir();
            try {
                final FileHandle file = fs.obtain("FirstSpirit Connect for Commerce.properties");
                final Properties props = new Properties();
                props.load(file.load());

                final GeneralConfig generalConfig = new GeneralConfig(props.getProperty("bridge.content-creator-extension"),
                                                                      Boolean.parseBoolean(props.getProperty("general.use-content-creator-extension")),
                                                                      Boolean.parseBoolean(props.getProperty("checkbox.disable-bridge-page-creation")));
                final BridgeConfig bridgeConfig = new BridgeConfig(props.getProperty("bridge.api-url"), props.getProperty("bridge.username"), props.getProperty("bridge.password"));
                final ReportConfig reportConfig = new ReportConfig(Integer.parseInt(props.getProperty("report.category-levels")), Integer.parseInt(props.getProperty("report.product-levels")));

                final ProjectAppConfiguration projectAppConfiguration = new ProjectAppConfiguration(generalConfig, bridgeConfig, reportConfig);

                final ProjectAppConfigurationService projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(environment.getBroker());
                projectAppConfigurationService.storeConfiguration(projectAppConfiguration);
                Logging.logInfo("Project app configuration for project with ID " + environment.getProjectId() + " successfully migrated!", getClass());
            } catch (IOException e) {
                Logging.logError("Failed migration of project app configuration for project with ID " + environment.getProjectId() + "!", e, getClass());
            }
        }
    }

    protected boolean isConfigMigrationNeeded(final String oldVersionString) {
        if (!Strings.isEmpty(oldVersionString)) {
            String[] splittedOldVersionString;
            // Splitting the old version string at the dots
            if (oldVersionString.contains("-SNAPSHOT")) {
                // Consider everything up to -SNAPSHOT
                splittedOldVersionString = oldVersionString.substring(0, oldVersionString.indexOf('-')).split("\\.");
            } else {
                splittedOldVersionString = oldVersionString.split("\\.");
            }

            // Splitting the breaking version string at the dots
            String[] splittedConfigBreakingVersion = CONFIG_BREAKING_VERSION.split("\\.");

            // Compare major, minor and patch version of the old version and the breaking version
            if (splittedOldVersionString[0].compareTo(splittedConfigBreakingVersion[0]) < 0) {
                return true;
            } else if (splittedOldVersionString[0].compareTo(splittedConfigBreakingVersion[0]) == 0
                       && splittedOldVersionString[1].compareTo(splittedConfigBreakingVersion[1]) < 0) {
                return true;
            } else
                return splittedOldVersionString[0].compareTo(splittedConfigBreakingVersion[0]) == 0
                       && splittedOldVersionString[1].compareTo(splittedConfigBreakingVersion[1]) == 0
                       && splittedOldVersionString[2].compareTo(splittedConfigBreakingVersion[2]) < 0;
        }

        return false;
    }
}
