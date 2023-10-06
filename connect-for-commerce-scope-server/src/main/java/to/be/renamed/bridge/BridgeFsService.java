package to.be.renamed.bridge;

import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import to.be.renamed.module.projectconfig.model.BridgeConfig;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface of the BridgeFsService.
 */
public interface BridgeFsService {

    /**
     * Configures the bridge instance for the project with the given projectId using the passed BridgeConfig.
     * @param projectId The id of the project for which the bridge instance is configured.
     * @param bridgeConfig The BridgeConfig which is used to configure the bridge instance
     */
    void configureBridge(Long projectId, BridgeConfig bridgeConfig);

    /**
     * Invalidates the http clients response cache of the bridge instance for the given projectId.
     * @param projectId The id of the project for which the cache is invalidated.
     */
    void invalidateCache(Long projectId);

    // Category

    /**
     * Returns a list of categories from the shop backend that match the given categoryIds.
     * @param projectId The id of the project for which the request is executed.
     * @param categoryIds The ids of the desired categories.
     * @param lang The language of the result.
     * @return A list of categories from the shop backend that match the given categoryIds.
     */
    List<EcomCategory> getCategories(Long projectId, Collection<String> categoryIds, @Nullable String lang);

    /**
     * Returns all available categories from the shop backend in the specified language.
     * @param projectId The id of the project for which the request is executed.
     * @param parentId Filter by parentId. It returns all related categories and subcategories from the category tree.
     * @param lang The language of the result.
     * @return The available categories from the shop backend in the specified language.
     */
    List<EcomCategory> findCategories(Long projectId, @Nullable String parentId, @Nullable String lang);

    /**
     * Returns the category tree in the specified language.
     * @param projectId The id of the project for which the request is executed.
     * @param lang The language of the result.
     * @return The category tree in the specified language.
     */
    Map<String, EcomCategory> getCategoriesTree(Long projectId, String lang);

    /**
     * Checks if the category tree endpoint is available.
     * @param projectId The id of the project for which the request is executed.
     * @return True if available.
     */
    boolean hasCategoryTree(Long projectId);

    // Product

    /**
     * Returns a list of products from the shop backend which match the given productIds.
     * @param projectId The id of the project for which the request is executed.
     * @param productIds The ids of the desired products.
     * @param lang The language of the result.
     * @return A list of products from the shop backend that match the given productIds.
     */
    List<EcomProduct> getProducts(Long projectId, Collection<String> productIds, @Nullable String lang);

    /**
     * Returns a pageable list of products from the shop backend which can be filtered using the query parameters listed below.
     * @param projectId The id of the project for which the request is executed.
     * @param q Fulltext search query string.
     * @param categoryId Filter by categoryId.
     * @param lang The language of the result.
     * @return A list of products from the shop backend which match the given filters.
     */
    List<EcomProduct> findProducts(Long projectId, @Nullable String q, @Nullable String categoryId, @Nullable String lang);

    // Content

    /**
     * Checks if the content endpoints are available.
     * @param projectId The id of the project for which the request is executed.
     * @return True if available
     */
    boolean hasContent(Long projectId);

    /**
     * Returns a list of content pages which match the given contentIds.
     * @param projectId The id of the project for which the request is executed.
     * @param contentIds The ids of the desired content pages.
     * @param lang The language of the result.
     * @return A list of content pages which match the given contentIds.
     */
    List<EcomContent> getContent(Long projectId, Collection<String> contentIds, @Nullable String lang);

    /**
     * Returns a list of content pages which can be filtered using the query parameters listed below.
     * @param projectId The id of the project for which the request is executed.
     * @param q Fulltext search query string.
     * @param lang The language of the result.
     * @return A list of content pages which match the given filters.
     */
    List<EcomContent> findContent(Long projectId, @Nullable String q, @Nullable String lang);

    /**
     * Creates a new page in the shop system.
     * @param projectId The id of the project for which the request is executed.
     * @param data The data of the page is created as a EcomElementDTO.
     * @return The id of the created page in the shop system.
     */
    String createContent(Long projectId, EcomElementDTO data);

    /**
     * Updates a page in the shop system.
     * @param projectId The id of the project for which the request is executed.
     * @param contentId The id of the to be updated page in the shop system.
     * @param data The data of the page is created as a EcomElementDTO.
     */
    void updateContent(Long projectId, String contentId, EcomElementDTO data);

    /**
     * Deletes a page in the shop system.
     * @param projectId The id of the project for which the request is executed.
     * @param contentId The id of the to be deleted page in the shop system.
     */
    void deleteContent(Long projectId, String contentId);

    /**
     * Returns the in the bridge instance cached value of hasContent().
     * @param projectId The id of the project for which the request is executed.
     * @return True is available.
     */
    boolean hasNewContentEndpoint(Long projectId);

    // Mapping

    /**
     * Returns the URL of the given EcomId stored in the shop system.
     * @param projectId The id of the project for which the request is executed.
     * @param ecomId The EcomId to get the URL for.
     * @return The URL of the given EcomId stored in the shop system.
     */
    String getStoreFrontUrl(Long projectId, EcomId ecomId);

    /**
     * Creates a EcomId object for the given URL.
     * @param projectId The id of the project for which the request is executed.
     * @param storeFrontUrl The URL to get the EcomId object from.
     * @return A EcomId object for the given URL
     */
    EcomId resolveStoreFrontUrl(Long projectId, String storeFrontUrl);

    // Test

    /**
     * Executes a bridge connection test.
     * @param projectId The id of the project for which the request is executed.
     * @param bridgeConfig The config to test with.
     * @param params.httpMethod Method used for requesting the targeted endpoint
     * @param params.url URL of the target endpoint
     * @param params.deprecated Marks the endpoint as isDeprecated using a notice inside the log
     * @return BridgeTestResult contains all the necessary data to display if the request was successful and,
     * if not, which problem lead to it. It's compatible with the TestConnectionSummary GUI.
     */
    BridgeTestResult testConnection(Long projectId, BridgeConfig bridgeConfig, TestConnectionRequest params);
}
