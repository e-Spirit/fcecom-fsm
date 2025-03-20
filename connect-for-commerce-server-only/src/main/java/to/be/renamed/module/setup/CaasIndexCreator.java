package to.be.renamed.module.setup;

import to.be.renamed.module.projectconfig.model.FieldsConfig;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.AdminService;
import de.espirit.firstspirit.access.ModuleAgent;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.access.admin.ProjectStorage;
import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Provides a run() method which creates a find page caas index for preview and release collection.
 */
public final class CaasIndexCreator {

    private static final String CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME = "CaasConnectCreateIndexExecutable";

    private SpecialistsBroker specialistsBroker;
    private FieldsConfig fieldsConfig;

    private final Map<String, Object> params = new HashMap<>();

    private long projectId;

    private CaasIndexCreator(final long projectId, final SpecialistsBroker specialistsBroker, final FieldsConfig fieldsConfig) {
        this.projectId = projectId;
        this.specialistsBroker = specialistsBroker;
        this.fieldsConfig = fieldsConfig;
    }

    /**
     * Creates a CaasIndexCreator instance.
     *
     * @param projectId         The id of the project the index should be created for.
     * @param fieldsConfig      The FieldsConfig telling the CaasIndexCreator how to name and configure the index.
     * @param specialistsBroker This broker is needed to get the project by projectId with the ModuleAdminAgent and to get the CaaS executable.
     * @return Instance of CaasIndexCreator, ready to be run.
     */
    public static CaasIndexCreator create(final long projectId, final SpecialistsBroker specialistsBroker, final FieldsConfig fieldsConfig) {
        return new CaasIndexCreator(projectId, specialistsBroker, fieldsConfig);
    }

    /**
     * Adds an index to the CaaS for better performance when querying fetchByFilter for the
     * findPage method in our Frontend API, based on the fields configured in the project app component.
     */
    public void run() {
        String typeField = fieldsConfig.getTypeField();
        String idField = fieldsConfig.getIdField();

        final AdminService adminService = specialistsBroker.requireSpecialist(ServicesBroker.TYPE).getService(AdminService.class);
        final Project project = adminService.getProjectStorage().getProject(projectId);
        if (project == null) {
            throw new CaasIndexCreatorException(format("Could not get project with id %d", projectId));
        }

        params.put("project", project);

        // An index name based on the configured fields.
        params.put("indexName", "idx_pageType_pageId_lang_country_with_%s_and_%s".formatted(typeField, idField));

        params.put("indexBody", """
            {
              "keys": {
                "page.formData.%s.value": 1,
                "page.formData.%s.value": 1,
                "locale.language": 1,
                "locale.country": 1
              }
            }
            """.formatted(typeField, idField));

        Class<? extends Executable> caasConnectCreateIndexExecutableClass = specialistsBroker.requireSpecialist(ModuleAgent.TYPE)
            .getTypeForName(CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME, Executable.class);

        if (caasConnectCreateIndexExecutableClass != null) {
            try {
                caasConnectCreateIndexExecutableClass
                    .getDeclaredConstructor()
                    .newInstance()
                    .execute(params);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new CaasIndexCreatorException(format("Could not create %s instance.", CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME), e);
            }
        } else {
            Logging.logInfo(
                format("Could not find executable class %s. No CaaS index will be created.", CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME),
                getClass());
        }
    }

    // ------ For Testing ------

    public CaasIndexCreator() {
    }

    Map<String, Object> getParams() {
        return params;
    }

    void setProjectId(long projectId) {
        this.projectId = projectId;
    }

}
