package to.be.renamed.module.setup;

import de.espirit.firstspirit.access.ModuleAgent;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.module.ProjectEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Provides a run() method which creates a find page caas index for preview and release collection.
 */
public final class CaasIndexCreator {

    private static final String CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME = "CaasConnectCreateIndexExecutable";

    private static final String FIND_PAGE_INDEX_NAME = "idx_pageType_pageId_lang_country";
    private static final String
        FIND_PAGE_INDEX =
        "{\"keys\": {\"page.formData.type.value\": 1, \"page.formData.id.value\": 1, \"locale.language\": 1, \"locale.country\": 1}}";

    private final ProjectEnvironment projectEnvironment;

    private CaasIndexCreator(final ProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment;
    }

    public static CaasIndexCreator create(final ProjectEnvironment projectEnvironment) {
        return new CaasIndexCreator(projectEnvironment);
    }

    private static Executable getCaasConnectCreateIndexExecutable(final ProjectEnvironment projectEnvironment) {
        Class<? extends Executable> caasConnectCreateIndexExecutableClass = projectEnvironment
            .getBroker()
            .requireSpecialist(ModuleAgent.TYPE)
            .getTypeForName(CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME, Executable.class);

        if (caasConnectCreateIndexExecutableClass != null) {
            try {
                return caasConnectCreateIndexExecutableClass
                    .getDeclaredConstructor()
                    .newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new CaasIndexCreatorException(format("Could not create %s instance.", CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME), e);
            }
        }
        return null;
    }

    private void createFindPageIndex() {
        Map<String, Object> params = new HashMap<>();
        params.put("project", projectEnvironment.getProject());
        params.put("indexName", FIND_PAGE_INDEX_NAME);
        params.put("indexBody", FIND_PAGE_INDEX);

        Executable executable = getCaasConnectCreateIndexExecutable(projectEnvironment);

        if (executable != null) {
            executable.execute(params);
        } else {
            throw new CaasIndexCreatorException(format("Could not get executable class %s.", CAAS_CONNECT_CREATE_INDEX_EXECUTABLE_NAME));
        }
    }

    /**
     * Creates a find page caas index for preview and release collection.
     */
    public void run() {
        createFindPageIndex();
    }
}
