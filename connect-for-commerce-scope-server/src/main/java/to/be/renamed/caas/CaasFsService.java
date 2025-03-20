package to.be.renamed.caas;

import to.be.renamed.module.projectconfig.model.FieldsConfig;

/**
 * Interface of the CaasFsService.
 */
public interface CaasFsService {

    /**
     * Adds an index to the CaaS for better performance when querying fetchByFilter for the
     * findPage method in our Frontend API, based on the fields configured in the project app component.
     *
     * @param projectId    The id of the project the index should be created for.
     * @param fieldsConfig The FieldsConfig telling the CaasIndexCreator how to name and configure the index.
     */
    void addCaasIndex(final long projectId, FieldsConfig fieldsConfig);

}
