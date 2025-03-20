package to.be.renamed.caas;

import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.projectconfig.model.FieldsConfig;
import to.be.renamed.module.setup.CaasIndexCreator;
import com.espirit.moddev.components.annotations.ServiceComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;

import static to.be.renamed.caas.CaasFsServiceImpl.SERVICE_NAME;

/**
 * FirstSpirit Service for processing CaaS actions.
 */
@ServiceComponent(name = SERVICE_NAME, displayName = ProjectAppHelper.MODULE_NAME + " - Service: Caas")
public class CaasFsServiceImpl implements CaasFsService, Service<CaasFsService> {

    protected static final String SERVICE_NAME = ProjectAppHelper.MODULE_NAME + " CaasFsService";

    private boolean running;
    private ServerEnvironment serverEnvironment;

    @Override
    public void addCaasIndex(final long projectId, final FieldsConfig fieldsConfig) {
        CaasIndexCreator.create(projectId, serverEnvironment.getBroker(), fieldsConfig).run();
    }

    // FS Service Overrides
    @Override
    public void start() {
        running = true;
        Logging.logInfo(String.format("Started %s", SERVICE_NAME), getClass());
    }

    @Override
    public void stop() {
        running = false;
        Logging.logInfo(String.format("Stopped %s", SERVICE_NAME), getClass());
    }

    @Override
    public boolean isRunning() {
        Logging.logInfo(running ? String.format("%s is running", SERVICE_NAME) : String.format("%s is stopped", SERVICE_NAME), getClass());
        return running;
    }

    @Override
    public Class<? extends CaasFsService> getServiceInterface() {
        return CaasFsService.class;
    }

    // currently no proxy is implemented
    @Override
    public Class<? extends ServiceProxy<CaasFsService>> getProxyClass() {
        return null;
    }

    @Override
    public void init(ServiceDescriptor serviceDescriptor, ServerEnvironment serverEnvironment) {
        this.serverEnvironment = serverEnvironment;
        Logging.logInfo(String.format("Initialized %s", SERVICE_NAME), getClass());
    }

    @Override
    public void installed() {
        Logging.logInfo(String.format("Installed %s", SERVICE_NAME), getClass());
    }

    @Override
    public void uninstalling() {
        Logging.logInfo(String.format("Uninstalled %s", SERVICE_NAME), getClass());
    }

    @Override
    public void updated(String s) {
        Logging.logInfo(String.format("Updated %s", SERVICE_NAME), getClass());
    }
}
