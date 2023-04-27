package to.be.renamed.module.projectconfig.access;

import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;

import de.espirit.firstspirit.agency.SpecialistsBroker;

public interface ProjectAppConfigurationService {

    /**
     * Initializes this service.
     * This method needs to be called before calling any of the service methods.
     * @param specialistsBroker {@link SpecialistsBroker} to access FirstSpirit
     * @param projectId Id of the project that this service should be bound to
     */
    void init(SpecialistsBroker specialistsBroker, long projectId);

    /**
     * Loads the project app configuration of the bound project.
     * @return the project app configuration of the bound project.
     */
    ProjectAppConfiguration loadConfiguration();

    /**
     * Stores the project app configuration of the bound project.
     * @param projectAppConfiguration the new project app configuration
     */
    void storeConfiguration(ProjectAppConfiguration projectAppConfiguration);

    /**
     * Returns the id of the project this service is bound too.
     * @return the id of the project this service is bound too.
     */
    long getProjectId();
}
