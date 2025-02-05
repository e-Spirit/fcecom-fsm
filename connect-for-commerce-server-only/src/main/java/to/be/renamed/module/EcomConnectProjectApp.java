package to.be.renamed.module;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.module.projectconfig.model.BridgeConfig;
import to.be.renamed.module.projectconfig.model.CacheConfig;
import to.be.renamed.module.projectconfig.model.GeneralConfig;
import to.be.renamed.module.projectconfig.model.JwtConfig;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import to.be.renamed.module.projectconfig.model.ReportConfig;
import to.be.renamed.module.setup.CaasIndexCreator;
import com.espirit.moddev.components.annotations.ProjectAppComponent;

import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.io.FileHandle;
import de.espirit.firstspirit.io.FileSystem;
import de.espirit.firstspirit.module.ProjectApp;
import de.espirit.firstspirit.module.ProjectEnvironment;
import de.espirit.firstspirit.module.descriptor.ProjectAppDescriptor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Properties;

@ProjectAppComponent(
    name = ProjectAppHelper.PROJECT_APP_NAME,
    displayName = ProjectAppHelper.PROJECT_APP_NAME,
    configurable = EcomConnectProjectConfig.class
)
public class EcomConnectProjectApp implements ProjectApp {

    private static final String CONFIG_BREAKING_VERSION_CATEGORY_LEVEL_PRODUCT_LEVEL = new String("2.5.0");
    private static final String CONFIG_BREAKING_VERSION_BRIDGE_API_URL = new String("3.3.0");

    private ProjectEnvironment environment;

    @Override
    public void init(ProjectAppDescriptor descriptor, ProjectEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void installed() {
        CaasIndexCreator.create(environment).run();
    }

    @Override
    public void uninstalling() {
        // Not used
    }

    @Override
    public void updated(String oldVersionString) {
        Logging.logInfo("Updating project app for project with ID " + environment.getProjectId() + " from version " + oldVersionString, getClass());
        if (isConfigMigrationNeeded(oldVersionString, CONFIG_BREAKING_VERSION_CATEGORY_LEVEL_PRODUCT_LEVEL)) {
            Logging.logInfo("Starting migration of project app configuration for project with ID " + environment.getProjectId(), getClass());
            final Properties props = loadOldConfiguration();

            final GeneralConfig generalConfig = new GeneralConfig(props.getProperty("bridge.content-creator-extension"),
                                                                  Boolean.parseBoolean(props.getProperty("general.use-content-creator-extension")),
                                                                  Boolean.parseBoolean(props.getProperty("checkbox.disable-bridge-page-creation")));

            Logging.logError(props.toString(), getClass());

            final BridgeConfig bridgeConfig = new BridgeConfig(
                props.getProperty("bridge.api-url"),
                props.getProperty("bridge.username"),
                props.getProperty("bridge.password"),
                CacheConfig.fromStrings(
                    props.getProperty("cache.size"),
                    props.getProperty("cache.age")));

            int categoryLevels = 3;
            try {
                categoryLevels = Integer.parseInt(props.getProperty("report.category-levels"));
            } catch (final NumberFormatException nfe) {
                Logging.logInfo("No value for property report.category-levels found in old configuration file. Migrating to default value: 3",
                                getClass());
            }

            int productLevels = 3;
            try {
                productLevels = Integer.parseInt(props.getProperty("report.product-levels"));
            } catch (final NumberFormatException nfe) {
                Logging.logInfo("No value for property report.product-levels found in old configuration file. Migrating to default value: 3",
                                getClass());
            }

            final ReportConfig reportConfig = new ReportConfig(categoryLevels, productLevels);

            final ProjectAppConfiguration projectAppConfiguration = new ProjectAppConfiguration(generalConfig, bridgeConfig, reportConfig, new JwtConfig());

            storeMigratedConfiguration(projectAppConfiguration);
            Logging.logInfo("Project app configuration for project with ID " + environment.getProjectId() + " successfully migrated!", getClass());
        }

        if (isConfigMigrationNeeded(oldVersionString, CONFIG_BREAKING_VERSION_BRIDGE_API_URL)) {
            Logging.logInfo("Starting migration of project app configuration for project with ID " + environment.getProjectId(), getClass());
            ProjectAppConfiguration projectAppConfiguration = loadOldConfigurationFromJson();

            String bridgeApiUrl = projectAppConfiguration.getBridgeConfig().getBridgeApiUrl();
            if (!Strings.isEmpty(bridgeApiUrl)) {
                String migratedBridgeApiUrl = bridgeApiUrl + "/api";

                final GeneralConfig generalConfig = projectAppConfiguration.getGeneralConfig();
                final BridgeConfig
                    bridgeConfig =
                    new BridgeConfig(migratedBridgeApiUrl, projectAppConfiguration.getBridgeConfig().getBridgeUsername(),
                                     projectAppConfiguration.getBridgeConfig().getBridgePassword(),
                                     projectAppConfiguration.getBridgeConfig().getCacheConfig());
                final ReportConfig reportConfig = projectAppConfiguration.getReportConfig();

                final ProjectAppConfiguration migratedConfiguration = new ProjectAppConfiguration(generalConfig, bridgeConfig, reportConfig, new JwtConfig());

                storeMigratedConfiguration(migratedConfiguration);
                Logging.logInfo("Project app configuration for project with ID " + environment.getProjectId() + " successfully migrated!",
                                getClass());
            } else {
                Logging.logInfo("No value for property bridge.api-url found in old configuration file. Leaving value empty.", getClass());
            }
        }
        CaasIndexCreator.create(environment).run();
    }

    protected void storeMigratedConfiguration(final ProjectAppConfiguration projectAppConfiguration) {
        final ProjectAppConfigurationService
            projectAppConfigurationService =
            ServiceFactory.getProjectAppConfigurationService(environment.getBroker());
        projectAppConfigurationService.storeConfiguration(projectAppConfiguration);
    }

    protected Properties loadOldConfiguration() {
        final FileSystem<? extends FileHandle> fs = environment.getConfDir();
        final Properties props = new Properties();
        try {
            final FileHandle file = fs.obtain("FirstSpirit Connect for Commerce.properties");
            props.load(file.load());
        } catch (IOException e) {
            Logging.logError("Failed migration of project app configuration for project with ID " + environment.getProjectId() + "!", e, getClass());
        }
        return props;
    }

    protected ProjectAppConfiguration loadOldConfigurationFromJson() {
        final FileSystem<? extends FileHandle> fs = environment.getConfDir();
        final ProjectAppConfigurationService
            projectAppConfigurationService =
            ServiceFactory.getProjectAppConfigurationService(environment.getBroker());
        ProjectAppConfiguration projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
        return projectAppConfiguration;
    }

    protected JsonObject loadOldJsonConfiguration() {
        final ProjectAppConfigurationService
            projectAppConfigurationService =
            ServiceFactory.getProjectAppConfigurationService(environment.getBroker());
        ProjectAppConfiguration projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
        projectAppConfigurationService.storeConfiguration(projectAppConfiguration);
        final FileSystem<? extends FileHandle> fs = environment.getConfDir();
        JsonObject props = new JsonObject();
        try {
            final FileHandle file = fs.obtain("FirstSpirit Connect for Commerce.json");
            props = (JsonObject) new JsonParser().parse(new InputStreamReader(file.load(), "UTF-8"));
        } catch (IOException e) {
            Logging.logError("Failed migration of project app configuration for project with ID " + environment.getProjectId() + "!", e, getClass());
        }
        return props;
    }

    protected boolean isConfigMigrationNeeded(final String oldVersionString, final String newVersionString) {
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
            String[] splittedConfigBreakingVersion = newVersionString.split("\\.");

            try {
                // Parse split versions as integer
                int[] splittedOldVersionInt = parseStringArrayToIntArray(splittedOldVersionString);
                int[] splittedConfigBreakingVersionInt = parseStringArrayToIntArray(splittedConfigBreakingVersion);

                // Compare major, minor and patch version of the old version and the breaking version
                if (splittedOldVersionInt[0] < splittedConfigBreakingVersionInt[0]) {
                    return true;
                } else if (splittedOldVersionInt[0] == splittedConfigBreakingVersionInt[0]
                           && splittedOldVersionInt[1] < splittedConfigBreakingVersionInt[1]) {
                    return true;
                } else {
                    return splittedOldVersionInt[0] == splittedConfigBreakingVersionInt[0]
                           && splittedOldVersionInt[1] == splittedConfigBreakingVersionInt[1]
                           && splittedOldVersionInt[2] < splittedConfigBreakingVersionInt[2];
                }
            } catch (NumberFormatException e) {
                Logging.logError("Could not determine if migration is needed, because version is not readable.", e, getClass());
                return false;
            }
        }

        return false;
    }

    protected int[] parseStringArrayToIntArray(String[] stringArray) throws NumberFormatException {
        return Arrays.stream(stringArray)
            .mapToInt(Integer::parseInt)
            .toArray();
    }
}

