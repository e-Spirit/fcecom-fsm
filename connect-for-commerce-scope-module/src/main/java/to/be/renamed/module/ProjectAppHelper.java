package to.be.renamed.module;

import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.agency.ModuleAdminAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import java.util.Collection;

public class ProjectAppHelper {

    /**
     * Module identifier (see {@code module.xml}).
     */
    public static final String MODULE_NAME = "FirstSpirit Connect for Commerce";

    /**
     * Project app identifier (see {@code module.xml}).
     */
    public static final String PROJECT_APP_NAME = MODULE_NAME + " - Project Configuration";
    public static final String PROJECT_APP_CONFIG_FILE = "configuration.json";

    private ProjectAppHelper() {
    }

    /**
     * Checks whether the project app is installed.
     *
     * @param specialistsBroker SpecialistsBroker to be used.
     * @param projectId         Id of the project to be checked.
     * @return true, if the project app is installed, false otherwise.
     */
    public static boolean isInstalled(final SpecialistsBroker specialistsBroker, final long projectId) {
        final ModuleAdminAgent moduleAdminAgent = specialistsBroker.requireSpecialist(ModuleAdminAgent.TYPE);
        final Collection<Project> projectAppUsages =
            moduleAdminAgent.getProjectAppUsages(MODULE_NAME, PROJECT_APP_NAME);

        return projectAppUsages.stream().anyMatch(project -> project.getId() == projectId);
    }
}
