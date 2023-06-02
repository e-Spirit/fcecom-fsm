package to.be.renamed.bridge;

import to.be.renamed.bridge.client.BridgeRequest;
import to.be.renamed.bridge.client.Json;
import to.be.renamed.bridge.client.PagedBridgeRequest;
import to.be.renamed.bridge.client.UnirestConnector;
import to.be.renamed.bridge.client.UnirestInterceptor;
import to.be.renamed.module.projectconfig.model.BridgeConfig;
import de.espirit.common.base.Logging;
import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.json.JsonObject;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpStatus;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class EcomBridgeApi {

    private final Map<String, EcomCategory> categories = new LinkedHashMap<>();

    private final UnirestConnector unirestConnector;

    /**
     * boolean needed to save information if the new Content Endpoint already exists in the Bridge
     */
    private boolean hasNewContentEndpoint;

    private EcomBridgeApi(BridgeConfig bridgeConfig) {
        unirestConnector = UnirestConnector.create(bridgeConfig);
        unirestConnector.interceptWith(new UnirestInterceptor());
        this.hasNewContentEndpoint = hasContent();
    }

    protected static EcomBridgeApi create(BridgeConfig bridgeConfig) {
        return new EcomBridgeApi(bridgeConfig);
    }

    protected void configure(BridgeConfig bridgeConfig) {
        unirestConnector.configureApiClient(bridgeConfig);
        hasNewContentEndpoint = hasContent();
    }

    void shutDownHttpClient() {
        unirestConnector.shutDown();
    }

    public void invalidateCache() {
        unirestConnector.getCache().invalidate();
        categories.clear();
    }

    /* Flat list of categories */
    public List<EcomCategory> getCategories(Collection<String> categoryIds, @Nullable String lang) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyList();
        }
        String endpointRoute = "/api/categories/ids/{categoryIds}";

        return BridgeRequest.bridgeRequest(
                unirestConnector.getHttpClient().get(endpointRoute)
                        .routeParam("categoryIds", Strings.implode(categoryIds, ","))
                        .queryString("lang", lang))
                .getItems()
                .stream().map(category -> new EcomCategory(new Json((JsonObject) category)))
                .filter(EcomCategory::isValid)
                .collect(toList());
    }

    public List<EcomCategory> findCategories(@Nullable String parentId, @Nullable String lang) {
        GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/categories/");

        if (parentId != null) {
            baseRequest.queryString("parentId", parentId);
        }
        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }
        return PagedBridgeRequest.pagedBridgeRequest(baseRequest, unirestConnector.getHttpClient())
                .getItems()
                .stream()
                .map(category -> new EcomCategory(new Json((JsonObject) category)))
                .filter(EcomCategory::isValid)
                .collect(Collectors.toList());
    }

    private void flattenCategories(List<EcomCategory> input, Map<String, EcomCategory> output) {
        input.forEach(category -> {
            output.put(category.getId(), category);
            flattenCategories(category.getChildren(), output);
        });
    }

    public Map<String, EcomCategory> getCategoriesTree(String lang) {
        if (categories.isEmpty()) {
            final GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/categories/tree");

                baseRequest.queryString("lang", lang);

            flattenCategories(BridgeRequest.bridgeRequest(baseRequest)
                    .getItems()
                    .stream().map(category -> new EcomCategory(new Json((JsonObject) category)))
                    .filter(EcomCategory::isValid)
                    .collect(toList()), categories);
        }
        return categories;
    }

    public boolean hasCategoryTree() {
        final int status = BridgeRequest.bridgeRequest(unirestConnector.getHttpClient().head("/api/categories/tree")).perform();
        return isStatusOk(status);
    }

    public List<EcomProduct> getProducts(Collection<String> productIds, @Nullable String lang) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        String endpointRoute = "/api/products/ids/{productIds}";

        final GetRequest baseRequest = unirestConnector.getHttpClient().get(endpointRoute);
        baseRequest.routeParam("productIds", Strings.implode(productIds, ","));

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return BridgeRequest.bridgeRequest(baseRequest)
                .getItems()
                .stream().map(product -> new EcomProduct(new Json((JsonObject) product)))
                .filter(EcomProduct::isValid)
                .collect(toList());
    }

    public List<EcomProduct> findProducts(@Nullable String q, @Nullable String categoryId, @Nullable String lang) {
        final GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/products/");

        if (q != null) {
            baseRequest.queryString("q", q);
        }
        if (categoryId != null) {
            baseRequest.queryString("categoryId", categoryId);
        }
        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return PagedBridgeRequest.pagedBridgeRequest(baseRequest, unirestConnector.getHttpClient())
                .getItems()
                .stream().map(product -> new EcomProduct(new Json((JsonObject) product)))
                .filter(EcomProduct::isValid)
                .collect(toList());
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean hasContents() {
        final int status = BridgeRequest.bridgeRequest(unirestConnector.getHttpClient().head("/api/contentpages")).perform();
        return isStatusOk(status);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<EcomContent> getContents(Collection<String> contentIds, @Nullable String lang) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }

        final GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/contentpages/ids/{contentIds}");
        baseRequest.routeParam("contentIds", Strings.implode(contentIds, ","));

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return BridgeRequest.bridgeRequest(baseRequest)
                .getItems()
                .stream().map(content -> new EcomContent(new Json((JsonObject) content)))
                .filter(EcomContent::isValid)
                .collect(toList());
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<EcomContent> findContents(@Nullable String q, @Nullable String lang) {
        final GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/contentpages/");

        if (q != null) {
            baseRequest.queryString("q", q);
        }
        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return PagedBridgeRequest.pagedBridgeRequest(baseRequest, unirestConnector.getHttpClient())
                .getItems()
                .stream().map(content -> new EcomContent(new Json((JsonObject) content)))
                .filter(EcomContent::isValid)
                .collect(toList());
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String createContentPage(EcomElementDTO data, String lang) {
        invalidateCache();
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClient().post("/api/contentpages/");

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return BridgeRequest.bridgeRequest(baseRequest
                .body(BridgeUtilities.toJSONObject(data.getOldJsonModel()))
                .header("Content-Type", "application/json"))
                .getItem()
                .get("id");
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void updateContentPage(String contentId, EcomElementDTO data, String lang) {
        invalidateCache();
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClient().put("/api/contentpages/{contentId}");

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        BridgeRequest.bridgeRequest(baseRequest
                .header("Content-Type", "application/json")
                .body(BridgeUtilities.toJSONObject(data.getOldJsonModel()))
                .routeParam("contentId", contentId))
                .perform();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void deleteContentPage(String contentId, String lang) {
        invalidateCache();
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClient().delete("/api/contentpages/{contentId}");

        baseRequest.routeParam("contentId", contentId);

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        BridgeRequest.bridgeRequest(baseRequest)
                .perform();
    }

    public final boolean hasContent() {
        final int status = BridgeRequest.bridgeRequest(unirestConnector.getHttpClient().head("/api/content")).perform();
        return isStatusOk(status);
    }

    public List<EcomContent> getContent(Collection<String> contentIds, @Nullable String lang) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }

        final GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/content/ids/{contentIds}");
        baseRequest.routeParam("contentIds", Strings.implode(contentIds, ","));

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return BridgeRequest.bridgeRequest(baseRequest)
                .getItems()
                .stream().map(content -> new EcomContent(new Json((JsonObject) content)))
                .filter(EcomContent::isValid)
                .collect(toList());
    }


    public List<EcomContent> findContent(@Nullable String q, @Nullable String lang) {
        final GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/content/");

        if (q != null) {
            baseRequest.queryString("q", q);
        }
        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }
        return PagedBridgeRequest.pagedBridgeRequest(baseRequest, unirestConnector.getHttpClient())
                .getItems()
                .stream().map(content -> new EcomContent(new Json((JsonObject) content)))
                .filter(EcomContent::isValid)
                .collect(toList());
    }

    public String createContent(EcomElementDTO data) {
        invalidateCache();
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClient().post("/api/content/");

        return BridgeRequest.bridgeRequest(baseRequest
                .body(BridgeUtilities.toJSONObject(data.getJsonModel()))
                .header("Content-Type", "application/json"))
                .getItem()
                .get("id");
    }

    public void updateContent(String contentId, EcomElementDTO data) {
        invalidateCache();
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClient().put("/api/content/{contentId}");

        BridgeRequest.bridgeRequest(baseRequest
                .header("Content-Type", "application/json")
                .body(BridgeUtilities.toJSONObject(data.getJsonModel()))
                .routeParam("contentId", contentId))
                .perform();
    }

    public void deleteContent(String contentId) {
        invalidateCache();
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClient().delete("/api/content/{contentId}");

        baseRequest.routeParam("contentId", contentId);

        BridgeRequest.bridgeRequest(baseRequest)
                .perform();
    }

    public boolean hasNewContentEndpoint() {
        return hasNewContentEndpoint;
    }

    public String getStoreFrontUrl(EcomId ecomId) {
        if (ecomId.isValid()) {
            final GetRequest baseRequest = unirestConnector.getHttpClient().get("/api/storefront-url");

            baseRequest.queryString("type", ecomId.getType());
            baseRequest.queryString("id", ecomId.getId());

            if (ecomId.getLang() != null) {
                baseRequest.queryString("lang", ecomId.getLang());
            }

            return BridgeRequest.bridgeRequest(baseRequest)
                    .getItem()
                    .get("url");
        }
        return null;
    }

    public EcomId resolveStoreFrontUrl(String storeFrontUrl) {
        return EcomId.from(BridgeRequest.bridgeRequest(
                unirestConnector.getHttpClient().get("/api/lookup-url")
                        .queryString("url", storeFrontUrl))
                .getItem());
    }

    public static String testConnection(UnirestConnector connector) {
        Logging.logDebug("Performing Bridge Test", EcomBridgeApi.class);

        StringBuilder summary = new StringBuilder();
        connector.interceptWith(new UnirestInterceptor(summary));

        BridgeRequest.bridgeRequest(connector.getHttpClient().head("/api/categories/tree")).perform();
        BridgeRequest.bridgeRequest(connector.getHttpClient().head("/api/content")).perform();
        BridgeRequest.bridgeRequest(connector.getHttpClient().head("/api/contentpages")).perform();

        connector.getHttpClient().shutDown();

        return summary.toString();
    }

    public String testConnection(BridgeConfig bridgeConfig) {
        // create a new http client to not override the config of the current one
        return testConnection(UnirestConnector.create(bridgeConfig));
    }

    /**
     * Returns true if the given status code is between 200 and 300
     * @param statusCode Input to evaluate
     * @return Result
     */
    private static boolean isStatusOk(int statusCode) {
        return statusCode >= HttpStatus.OK && statusCode < HttpStatus.MULTIPLE_CHOICE;
    }
}
