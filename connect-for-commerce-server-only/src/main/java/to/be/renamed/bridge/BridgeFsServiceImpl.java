package to.be.renamed.bridge;

import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import to.be.renamed.module.projectconfig.model.BridgeConfig;
import com.espirit.moddev.components.annotations.ServiceComponent;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.module.ServerEnvironment;
import de.espirit.firstspirit.module.Service;
import de.espirit.firstspirit.module.ServiceProxy;
import de.espirit.firstspirit.module.descriptor.ServiceDescriptor;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static to.be.renamed.bridge.BridgeFsServiceImpl.SERVICE_NAME;

/**
 * FirstSpirit Service for managing EcomBridgeApi instances and the delegation of bridge requests.
 */
@ServiceComponent(name = SERVICE_NAME, displayName = ProjectAppHelper.MODULE_NAME + " - Service: Bridge")
public class BridgeFsServiceImpl implements BridgeFsService, Service<BridgeFsService> {
    protected static final String SERVICE_NAME = ProjectAppHelper.MODULE_NAME + " BridgeFsService";

    private boolean running;

    private SpecialistsBroker broker;

    private final Map<Long, EcomBridgeApi> bridges = new HashMap<>();

    // Needed for injecting a mock EcomBridgeApi while testing
    protected final void addBridge(Long projectId, EcomBridgeApi ecomBridgeApi) {
        bridges.put(projectId, ecomBridgeApi);
    }

    private EcomBridgeApi getBridgeInstance(long projectId) {
        try {
            bridges.computeIfAbsent(projectId, pId -> {
                Logging.logDebug(String.format("Creating Bridge for project '%s'", projectId), getClass());
                return bridges.put(pId, EcomBridgeApi.create(ServiceFactory
                        .getProjectAppConfigurationService(broker, pId)
                        .loadConfiguration()
                        .getBridgeConfig()));
            });
        } catch (ConcurrentModificationException e) {
            Logging.logDebug(String.format("%s %s Map already being computed, trying again.", e.getClass().getName(), e.getMessage()), getClass());
            return getBridgeInstance(projectId);
        }
        return bridges.get(projectId);
    }

    private void shutDownUnirestInstances() {
        bridges.values().forEach(EcomBridgeApi::shutDownHttpClient);
        Logging.logDebug("Shut down unirest instances", getClass());
    }

    // Utility

    public void configureBridge(Long projectId, BridgeConfig bridgeConfig) {
        getBridgeInstance(projectId).configure(bridgeConfig);
    }

    public void invalidateCache(Long projectId) {
        getBridgeInstance(projectId).invalidateCache();
    }

    // Category

    /* Flat list of categories */
    public List<EcomCategory> getCategories(Long projectId, Collection<String> categoryIds, @Nullable String lang) {
        return getBridgeInstance(projectId).getCategories(categoryIds, lang);
    }

    public List<EcomCategory> findCategories(Long projetId, @Nullable String parentId, @Nullable String lang) {
        return getBridgeInstance(projetId).findCategories(parentId, lang);
    }

    public Map<String, EcomCategory> getCategoriesTree(Long projectId, String lang) {
        return getBridgeInstance(projectId).getCategoriesTree(lang);
    }

    public boolean hasCategoryTree(Long projectId) {
        return getBridgeInstance(projectId).hasCategoryTree();
    }

    // Product

    public List<EcomProduct> getProducts(Long projectId, Collection<String> productIds, @Nullable String lang) {
        return getBridgeInstance(projectId).getProducts(productIds, lang);
    }

    public List<EcomProduct> findProducts(Long projectId, @Nullable String q, @Nullable String categoryId, @Nullable String lang) {
        return getBridgeInstance(projectId).findProducts(q, categoryId, lang);
    }

    // Content

    public final boolean hasContent(Long projectId) {
        return getBridgeInstance(projectId).hasContent();
    }

    public List<EcomContent> getContent(Long projectId, Collection<String> contentIds, @Nullable String lang) {
        return getBridgeInstance(projectId).getContent(contentIds, lang);
    }

    public List<EcomContent> findContent(Long projectId, @Nullable String q, @Nullable String lang) {
        return getBridgeInstance(projectId).findContent(q, lang);
    }

    public String createContent(Long projectId, EcomElementDTO data) {
        return getBridgeInstance(projectId).createContent(data);
    }

    public void updateContent(Long projectId, String contentId, EcomElementDTO data) {
        getBridgeInstance(projectId).updateContent(contentId, data);
    }

    public void deleteContent(Long projectId, String contentId) {
        getBridgeInstance(projectId).deleteContent(contentId);
    }

    public boolean hasNewContentEndpoint(Long projectId) {
        return getBridgeInstance(projectId).hasNewContentEndpoint();
    }

    public String getStoreFrontUrl(Long projectId, EcomId ecomId) {
        return getBridgeInstance(projectId).getStoreFrontUrl(ecomId);
    }

    public EcomId resolveStoreFrontUrl(Long projectId, String storeFrontUrl) {
        return getBridgeInstance(projectId).resolveStoreFrontUrl(storeFrontUrl);
    }

    public BridgeTestResult testConnection(Long projectId, BridgeConfig bridgeConfig, TestConnectionRequest params) {
        return getBridgeInstance(projectId).testConnection(bridgeConfig, params);
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
    public Class<? extends BridgeFsService> getServiceInterface() {
        return BridgeFsService.class;
    }

    // currently no proxy is implemented
    @Override
    public Class<? extends ServiceProxy<BridgeFsService>> getProxyClass() {
        return null;
    }

    @Override
    public void init(ServiceDescriptor serviceDescriptor, ServerEnvironment serverEnvironment) {
        broker = serverEnvironment.getBroker();
        Logging.logInfo(String.format("Initialized %s", SERVICE_NAME), getClass());
    }

    @Override
    public void installed() {
        Logging.logInfo(String.format("Installed %s", SERVICE_NAME), getClass());
    }

    @Override
    public void uninstalling() {
        shutDownUnirestInstances();
        bridges.clear();
        Logging.logInfo(String.format("Uninstalled %s", SERVICE_NAME), getClass());
    }

    @Override
    public void updated(String s) {
        shutDownUnirestInstances();
        bridges.clear();
        Logging.logInfo(String.format("Updated %s", SERVICE_NAME), getClass());
    }
}
