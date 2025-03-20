package to.be.renamed.caas;

import to.be.renamed.module.projectconfig.model.FieldsConfig;

import de.espirit.firstspirit.module.ProjectEnvironment;

/**
 * Interface of the CaasService.
 */
public interface CaasService {

    /**
     * Initializes the CaasService with the needed project environment.
     *
     * @param projectEnvironment of the current project.
     */
    void init(ProjectEnvironment projectEnvironment);

    /**
     * Adds a CaaS index based on the form fields configuration.
     *
     * @param fieldsConfig The FieldsConfig which the index is based on.
     */
    void addCaasIndex(FieldsConfig fieldsConfig);
}
