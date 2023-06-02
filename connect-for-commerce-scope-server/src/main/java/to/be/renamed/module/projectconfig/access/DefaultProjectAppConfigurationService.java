package to.be.renamed.module.projectconfig.access;

import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;

import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.agency.SpecialistsBroker;

public class DefaultProjectAppConfigurationService implements ProjectAppConfigurationService {

    private ProjectAppConfigurationFsService projectAppConfigurationAccessor;
    private long projectId;

    @Override
    public void init(final SpecialistsBroker specialistsBroker, final long projectId) {
        this.projectId = projectId;
        final ServicesBroker servicesBroker = specialistsBroker.requireSpecialist(ServicesBroker.TYPE);
        projectAppConfigurationAccessor = servicesBroker.getService(ProjectAppConfigurationFsService.class);
    }

    @Override
    public ProjectAppConfiguration loadConfiguration() {
        return projectAppConfigurationAccessor.loadConfiguration(projectId, ProjectAppConfiguration.class);
    }

    @Override
    public void storeConfiguration(ProjectAppConfiguration projectAppConfiguration) {
        projectAppConfigurationAccessor.storeConfiguration(projectId, projectAppConfiguration);
    }

    @Override
    public long getProjectId() {
        return projectId;
    }
}
