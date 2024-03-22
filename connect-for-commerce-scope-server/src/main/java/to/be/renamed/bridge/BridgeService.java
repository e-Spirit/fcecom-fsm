package to.be.renamed.bridge;

import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import to.be.renamed.module.projectconfig.model.BridgeConfig;

import de.espirit.firstspirit.agency.SpecialistsBroker;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BridgeService {

    void init(SpecialistsBroker broker, long projectId);

    /**
     * Updates the configuration of the http client in the facades regarding context.
     *
     * @param bridgeConfig The new BridgeConfig.
     */
    void configureBridge(BridgeConfig bridgeConfig);

    /**
     * Invalidates the response cache of the http client in the facades regarding context.
     */
    void invalidateCache();

    // Category

    /**
     * Returns a list of categories from the shop backend that match the given categoryIds.
     *
     * @param categoryIds The ids of the desired categories.
     * @param lang        The language of the result.
     * @return A list of categories from the shop backend that match the given categoryIds.
     */
    List<EcomCategory> getCategories(Collection<String> categoryIds, @Nullable String lang);

    /**
     * Returns all available categories from the shop backend in the specified language.
     *
     * @param parentId Filter by parentId. It returns all related categories and subcategories from the category tree.
     * @param lang     The language of the result.
     * @return The available categories from the shop backend in the specified language.
     */
    List<EcomCategory> findCategories(@Nullable String parentId, @Nullable String lang);

    /**
     * Returns the category tree in the specified language.
     *
     * @param lang The language of the result.
     * @return The category tree in the specified language.
     */
    Map<String, EcomCategory> getCategoriesTree(String lang);

    /**
     * Checks if the category tree endpoint is available.
     *
     * @return True if available.
     */
    boolean hasCategoryTree();

    // Product

    /**
     * Returns a list of products from the shop backend which match the given productIds.
     *
     * @param productIds The ids of the desired products.
     * @param lang       The language of the result.
     * @return A list of products from the shop backend that match the given productIds.
     */
    List<EcomProduct> getProducts(Collection<String> productIds, @Nullable String lang);

    /**
     * Returns a pageable list of products from the shop backend which can be filtered using the query parameters listed below.
     *
     * @param q          Fulltext search query string.
     * @param categoryId Filter by categoryId.
     * @param lang       The language of the result.
     * @return A list of products from the shop backend which match the given filters.
     */
    List<EcomProduct> findProducts(@Nullable String q, @Nullable String categoryId, @Nullable String lang);

    // Content

    /**
     * Checks if the content endpoints are available.
     *
     * @return True if available
     */
    boolean hasContent();

    /**
     * Returns a list of content pages which match the given contentIds.
     *
     * @param contentIds The ids of the desired content pages.
     * @param lang       The language of the result.
     * @return A list of content pages which match the given contentIds.
     */
    List<EcomContent> getContent(Collection<String> contentIds, @Nullable String lang);

    /**
     * Returns a list of content pages which can be filtered using the query parameters listed below.
     *
     * @param q    Fulltext search query string.
     * @param lang The language of the result.
     * @return A list of content pages which match the given filters.
     */
    List<EcomContent> findContent(@Nullable String q, @Nullable String lang);

    /**
     * Creates a new page in the shop system.
     *
     * @param data The data of the page is created as a EcomElementDTO.
     * @return The id of the created page in the shop system.
     */
    String createContent(EcomElementDTO data);

    /**
     * Updates a page in the shop system.
     *
     * @param contentId The id of the to be updated page in the shop system.
     * @param data      The data of the page is created as a EcomElementDTO.
     */
    void updateContent(String contentId, EcomElementDTO data);

    /**
     * Deletes a page in the shop system.
     *
     * @param contentId The id of the to be deleted page in the shop system.
     */
    void deleteContent(String contentId);

    // Mapping

    /**
     * Returns the URL of the given EcomId stored in the shop system.
     *
     * @param ecomId The EcomId to get the URL for.
     * @return The URL of the given EcomId stored in the shop system.
     */
    String getStoreFrontUrl(EcomId ecomId);

    /**
     * Creates a EcomId object for the given URL.
     *
     * @param storeFrontUrl The URL to get the EcomId object from.
     * @return A EcomId object for the given URL
     */
    EcomId resolveStoreFrontUrl(String storeFrontUrl);

    /**
     * Executes a bridge connection test.
     *
     * @param bridgeConfig      The config to test with.
     * @param params.httpMethod Method used for requesting the targeted endpoint
     * @param params.url        URL of the target endpoint
     * @param params.deprecated Marks the endpoint as isDeprecated using a notice inside the log
     * @return BridgeTestResult contains all the necessary data to display if the request was successful and,
     * if not, which problem lead to it. It's compatible with the TestConnectionSummary GUI.
     */
    BridgeTestResult testConnection(BridgeConfig bridgeConfig, TestConnectionRequest params);
}
