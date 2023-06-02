package to.be.renamed.module.projectconfig.access;

import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import com.espirit.moddev.components.annotations.ServiceComponent;
import com.espirit.moddev.fcaf.AccessedConfiguration;
import com.espirit.moddev.fcaf.ContextAwareConfigurationAccessor;
import com.espirit.moddev.fcaf.services.projectapp.ProjectAppConfigurationAccessorService;

import de.espirit.firstspirit.module.ServiceProxy;

/**
 * FirstSpirit {@link de.espirit.firstspirit.module.Service} that provides means to access the module configuration.
 */
@ServiceComponent(name = ProjectAppConfigurationFsServiceImpl.SERVICE_NAME, displayName = ProjectAppHelper.MODULE_NAME + " - Service: Configuration")
@AccessedConfiguration(moduleName = ProjectAppHelper.MODULE_NAME,
    componentName = ProjectAppHelper.PROJECT_APP_NAME,
    configurationFile = ProjectAppHelper.PROJECT_APP_CONFIG_FILE)
public class ProjectAppConfigurationFsServiceImpl extends ProjectAppConfigurationAccessorService<ProjectAppConfiguration>
    implements ProjectAppConfigurationFsService {

    static final String SERVICE_NAME = ProjectAppHelper.MODULE_NAME + " ConfigurationService";

    @Override
    public Class<? extends ContextAwareConfigurationAccessor<Long, ProjectAppConfiguration>> getServiceInterface() {
        return ProjectAppConfigurationFsService.class;
    }

    @Override
    public Class<? extends ServiceProxy<ContextAwareConfigurationAccessor<Long, ProjectAppConfiguration>>> getProxyClass() {
        return ProjectAppConfigurationFsServiceProxy.class;
    }
}
