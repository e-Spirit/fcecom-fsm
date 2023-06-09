package to.be.renamed.bridge;

import to.be.renamed.module.projectconfig.model.BridgeConfig;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Facade for the BridgeFsService.
 */
public class DefaultBridgeService implements BridgeService {
    private BridgeFsService bridgeFsService;

    private Long projectId;

    public DefaultBridgeService() {}
    // Needed for testing
    protected DefaultBridgeService(final SpecialistsBroker broker, Long projectId) {
        final ServicesBroker servicesBroker = broker.requireSpecialist(ServicesBroker.TYPE);
        bridgeFsService = servicesBroker.getService(BridgeFsService.class);
        this.projectId = projectId;
    }

    @Override
    public void init(final SpecialistsBroker broker, final long projectId) {
        final ServicesBroker servicesBroker = broker.requireSpecialist(ServicesBroker.TYPE);
        bridgeFsService = servicesBroker.getService(BridgeFsService.class);
        this.projectId = projectId;
    }

    /**
     * Updates the configuration of the http client in the facades regarding context.
     * @param bridgeConfig The new BridgeConfig.
     */
    public void configureBridge(BridgeConfig bridgeConfig) {
        bridgeFsService.configureBridge(projectId, bridgeConfig);
    }

    /**
     * Invalidates the response cache of the http client in the facades regarding context.
     */
    public void invalidateCache() {
        bridgeFsService.invalidateCache(projectId);
    }

    // Category

    /**
     * Returns a list of categories from the shop backend that match the given categoryIds.
     * @param categoryIds The ids of the desired categories.
     * @param lang The language of the result.
     * @return A list of categories from the shop backend that match the given categoryIds.
     */
    public List<EcomCategory> getCategories(Collection<String> categoryIds, @Nullable String lang) {
        return bridgeFsService.getCategories(projectId, categoryIds, lang);
    }

    /**
     * Returns all available categories from the shop backend in the specified language.
     * @param parentId Filter by parentId. It returns all related categories and subcategories from the category tree.
     * @param lang The language of the result.
     * @return The available categories from the shop backend in the specified language.
     */
    public List<EcomCategory> findCategories(@Nullable String parentId, @Nullable String lang) {
        return bridgeFsService.findCategories(projectId, parentId, lang);
    }

    /**
     * Returns the category tree in the specified language.
     * @param lang The language of the result.
     * @return The category tree in the specified language.
     */
    public Map<String, EcomCategory> getCategoriesTree(String lang) {
        return bridgeFsService.getCategoriesTree(projectId, lang);
    }

    /**
     * Checks if the category tree endpoint is available.
     * @return True if available.
     */
    public boolean hasCategoryTree() {
        return bridgeFsService.hasCategoryTree(projectId);
    }


    // Product

    /**
     * Returns a list of products from the shop backend which match the given productIds.
     * @param productIds The ids of the desired products.
     * @param lang The language of the result.
     * @return A list of products from the shop backend that match the given productIds.
     */
    public List<EcomProduct> getProducts(Collection<String> productIds, @Nullable String lang) {
        return bridgeFsService.getProducts(projectId, productIds, lang);
    }

    /**
     * Returns a pageable list of products from the shop backend which can be filtered using the query parameters listed below.
     * @param q Fulltext search query string.
     * @param categoryId Filter by categoryId.
     * @param lang The language of the result.
     * @return A list of products from the shop backend which match the given filters.
     */
    public List<EcomProduct> findProducts(@Nullable String q, @Nullable String categoryId, @Nullable String lang) {
        return bridgeFsService.findProducts(projectId, q, categoryId, lang);
    }


    // Content

    /**
     * @deprecated
     * Checks if the contentpages endpoints are available.
     * @return True if available
     */
    @Deprecated
    public boolean hasContents() {
        return bridgeFsService.hasContents(projectId);
    }

    /**
     * @deprecated
     * Returns a list of content pages which match the given contentIds.
     * @param contentIds The ids of the desired content pages.
     * @param lang The language of the result.
     * @return A list of content pages which match the given contentIds.
     */
    @Deprecated
    public List<EcomContent> getContents(Collection<String> contentIds, @Nullable String lang) {
        return bridgeFsService.getContents(projectId, contentIds, lang);
    }

    /**
     * @deprecated
     * Returns a list of content pages which can be filtered using the query parameters listed below.
     * @param q Fulltext search query string.
     * @param lang The language of the result.
     * @return A list of content pages which match the given filters.
     */
    @Deprecated
    public List<EcomContent> findContents(@Nullable String q, @Nullable String lang) {
        return bridgeFsService.findContents(projectId, q, lang);
    }

    /**
     * @deprecated
     * Creates a new page in the shop system.
     * @param data The data of the page is created as a EcomElementDTO.
     * @param lang The language to localize the label.
     * @return The id of the created page in the shop system.
     */
    @Deprecated
    public String createContentPage(EcomElementDTO data, String lang) {
        Logging.logInfo("###### CP Facade", getClass());
        return bridgeFsService.createContentPage(projectId, data, lang);
    }

    /**
     * @deprecated
     * Updates a page in the shop system.
     * @param contentId The id of the to be updated page in the shop system.
     * @param data The data of the page is created as a EcomElementDTO.
     * @param lang The language to localize the label.
     */
    @Deprecated
    public void updateContentPage(String contentId, EcomElementDTO data, String lang) {
        bridgeFsService.updateContentPage(projectId, contentId, data, lang);
    }

    /**
     * @deprecated
     * Deletes a page in the shop system.
     * @param contentId The id of the to be deleted page in the shop system.
     * @param lang The language to localize the label.
     */
    @Deprecated
    public void deleteContentPage(String contentId, String lang) {
        bridgeFsService.deleteContentPage(projectId, contentId, lang);
    }

    /**
     * Checks if the content endpoints are available.
     * @return True if available
     */
    public boolean hasContent() {
        return bridgeFsService.hasContent(projectId);
    }

    /**
     * Returns a list of content pages which match the given contentIds.
     * @param contentIds The ids of the desired content pages.
     * @param lang The language of the result.
     * @return A list of content pages which match the given contentIds.
     */
    public List<EcomContent> getContent(Collection<String> contentIds, @Nullable String lang) {
        return bridgeFsService.getContent(projectId, contentIds, lang);
    }

    /**
     * Returns a list of content pages which can be filtered using the query parameters listed below.
     * @param q Fulltext search query string.
     * @param lang The language of the result.
     * @return A list of content pages which match the given filters.
     */
    public List<EcomContent> findContent(@Nullable String q, @Nullable String lang) {
        return bridgeFsService.findContent(projectId, q, lang);
    }

    /**
     * Creates a new page in the shop system.
     * @param data The data of the page is created as a EcomElementDTO.
     * @return The id of the created page in the shop system.
     */
    public String createContent(EcomElementDTO data) {
        return bridgeFsService.createContent(projectId, data);
    }

    /**
     * Updates a page in the shop system.
     * @param contentId The id of the to be updated page in the shop system.
     * @param data The data of the page is created as a EcomElementDTO.
     */
    public void updateContent(String contentId, EcomElementDTO data) {
        bridgeFsService.updateContent(projectId, contentId, data);
    }

    /**
     * Deletes a page in the shop system.
     * @param contentId The id of the to be deleted page in the shop system.
     */
    public void deleteContent(String contentId) {
        bridgeFsService.deleteContent(projectId, contentId);
    }

    /**
     * Returns the in the bridge instance cached value of hasContent().
     * @return True is available.
     */
    public boolean hasNewContentEndpoint() {
        return bridgeFsService.hasNewContentEndpoint(projectId);
    }

    // Mapping

    /**
     * Returns the URL of the given EcomId stored in the shop system.
     * @param ecomId The EcomId to get the URL for.
     * @return The URL of the given EcomId stored in the shop system.
     */
    public String getStoreFrontUrl(EcomId ecomId) {
        return bridgeFsService.getStoreFrontUrl(projectId, ecomId);
    }

    /**
     * Creates a EcomId object for the given URL.
     * @param storeFrontUrl The URL to get the EcomId object from.
     * @return A EcomId object for the given URL
     */
    public EcomId resolveStoreFrontUrl(String storeFrontUrl) {
        return bridgeFsService.resolveStoreFrontUrl(projectId, storeFrontUrl);
    }

    // Test

    /**
     * Executes a bridge connection test.
     * @param bridgeConfig The config to test with.
     * @return The result as a String.
     */
    public String testConnection(BridgeConfig bridgeConfig) {
        return bridgeFsService.testConnection(projectId, bridgeConfig).toString();
    }
}
