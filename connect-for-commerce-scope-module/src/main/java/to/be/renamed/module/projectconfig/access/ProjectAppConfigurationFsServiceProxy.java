package to.be.renamed.module.projectconfig.access;

import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import com.espirit.moddev.fcaf.services.projectapp.ProjectAppConfigurationAccessorServiceProxy;

/**
 * Configuration service implementation.
 * Needed to get rid of the generics in the service implementation.
 */
public class ProjectAppConfigurationFsServiceProxy extends ProjectAppConfigurationAccessorServiceProxy<ProjectAppConfiguration>
    implements ProjectAppConfigurationFsService {

}
