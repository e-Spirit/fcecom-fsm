package to.be.renamed.caas;

import to.be.renamed.module.projectconfig.model.FieldsConfig;

import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.module.ProjectEnvironment;

/**
 * Facade for the CaasFsService.
 */
public class DefaultCaasService implements CaasService {

    private CaasFsService caasFsService;
    private long projectId;

    /**
     * Initializes the CaasService with the needed project environment.
     *
     * @param projectEnvironment The projectEnvironment of the current project.
     */
    @Override
    public void init(final ProjectEnvironment projectEnvironment) {
        final ServicesBroker servicesBroker = projectEnvironment.getBroker().requireSpecialist(ServicesBroker.TYPE);
        caasFsService = servicesBroker.getService(CaasFsService.class);
        projectId = projectEnvironment.getProjectId();
    }

    /**
     * Adds a CaaS index based on the form fields configuration.
     *
     * @param fieldsConfig The FieldsConfig which the index is based on.
     */
    @Override
    public void addCaasIndex(final FieldsConfig fieldsConfig) {
        caasFsService.addCaasIndex(projectId, fieldsConfig);
    }
}
