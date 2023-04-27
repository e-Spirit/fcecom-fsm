package to.be.renamed.module.projectconfig.access;

import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import com.espirit.moddev.fcaf.projectapp.ProjectAppConfigurationAccessor;

/**
 * This is just a helper interface, because in the service implementation we can not return the class of the generic type.
 */
public interface ProjectAppConfigurationFsService extends ProjectAppConfigurationAccessor<ProjectAppConfiguration> {

}
