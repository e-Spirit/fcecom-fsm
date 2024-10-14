package to.be.renamed.module;

import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.bridge.BridgeFsService;
import to.be.renamed.bridge.BridgeService;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Factory class providing different services to access module functionality.
 */
public final class ServiceFactory {

    private static final Class<ServiceFactory> LOGGER = ServiceFactory.class;

    private ServiceFactory() {
        // Hiding construction of this factory
    }

    /**
     * Gets a {@link ProjectAppConfigurationService} for the project that the given <code>specialistsBroker</code> is bound too.
     *
     * @param specialistsBroker {@link SpecialistsBroker} that is used to access FirstSpirit and to identify the project for which a service will be returned
     * @return a {@link ProjectAppConfigurationService} for the project that the given <code>specialistsBroker</code> is bound to.
     */
    public static ProjectAppConfigurationService getProjectAppConfigurationService(final SpecialistsBroker specialistsBroker) {
        return getProjectAppConfigurationService(specialistsBroker, specialistsBroker.requireSpecialist(ProjectAgent.TYPE).getId());
    }

    /**
     * Returns a {@link ProjectAppConfigurationService} for the project identified by the given <code>projectId</code>.
     *
     * @param specialistsBroker {@link SpecialistsBroker} that is used to access FirstSpirit
     * @param projectId         Id that identifies the project for which a service will be returned
     * @return a {@link ProjectAppConfigurationService} for the project identified by the given <code>projectId</code>.
     */
    public static ProjectAppConfigurationService getProjectAppConfigurationService(final SpecialistsBroker specialistsBroker, final long projectId) {
        logDebug("Received a request to provide a project app configuration service for project " + projectId + ".");
        try {
            final ProjectAppConfigurationService projectAppConfigurationService = loadProjectAppConfigurationService(specialistsBroker, projectId);
            logDebug("Successfully loaded a project app configuration service for project " + projectId + ".");
            return projectAppConfigurationService;
        } catch (final RuntimeException e) {
            throw new RuntimeException("Providing a project app configuration service for project " + projectId + " failed.", e);
        }
    }

    private static ProjectAppConfigurationService loadProjectAppConfigurationService(final SpecialistsBroker specialistsBroker,
                                                                                     final long projectId) {
        logDebug("Loading a project app configuration service for project " + projectId + ".");
        final ProjectAppConfigurationService service = load(ProjectAppConfigurationService.class);

        logDebug("Initializing project app configuration service for project " + projectId + ".");
        service.init(specialistsBroker, projectId);
        return service;
    }

    /**
     * Gets a {@link BridgeFsService} which executes requests on a bridge instance configured for the project that the given <code>specialistsBroker</code> is bound too.
     *
     * @param specialistsBroker {@link SpecialistsBroker} that is used to access FirstSpirit and to identify the project for which a service will be returned
     * @return a {@link BridgeFsService} which executes requests on a bridge instance configured for the project that the given <code>specialistsBroker</code> is bound too.
     */
    public static BridgeService getBridgeService(final SpecialistsBroker specialistsBroker) {
        return getBridgeService(specialistsBroker, specialistsBroker.requireSpecialist(ProjectAgent.TYPE).getId());
    }

    /**
     * Returns a {@link BridgeFsService} for the project identified by the given <code>projectId</code>.
     *
     * @param specialistsBroker {@link SpecialistsBroker} that is used to access FirstSpirit
     * @param projectId         Id that identifies the project for which a service will be returned
     * @return a {@link BridgeFsService} for the project identified by the given <code>projectId</code>.
     */
    public static BridgeService getBridgeService(final SpecialistsBroker specialistsBroker, final long projectId) {
        logDebug("Received a request to provide a bridge service for project " + projectId + ".");
        try {
            final BridgeService bridgeService = loadBridgeService(specialistsBroker, projectId);
            logDebug("Successfully loaded a bridge service for project " + projectId + ".");
            return bridgeService;
        } catch (final RuntimeException e) {
            throw new RuntimeException("Providing a bridge service for project " + projectId + " failed.", e);
        }
    }

    private static BridgeService loadBridgeService(final SpecialistsBroker specialistsBroker, final long projectId) {
        logDebug("Loading a bridge service for project " + projectId + ".");
        final BridgeService service = load(BridgeService.class);

        logDebug("Initializing bridge service for project " + projectId + ".");
        service.init(specialistsBroker, projectId);
        return service;
    }

    private static <ServiceT> ServiceT load(final Class<ServiceT> serviceClass) {

        logDebug("Loading service for '" + serviceClass + "'.");
        final Iterator<ServiceT> iterator = ServiceLoader.load(serviceClass, LOGGER.getClassLoader()).iterator();

        if (iterator.hasNext()) {
            final ServiceT service = iterator.next();
            logDebug("Loaded service implementation: " + service);
            return service;
        } else {
            throw new IllegalStateException("No implementation for '" + serviceClass + "' found.");
        }
    }

    private static void logDebug(final String message) {
        if (Logging.isDebugEnabled(LOGGER)) {
            Logging.logDebug(message, LOGGER);
        }
    }
}
