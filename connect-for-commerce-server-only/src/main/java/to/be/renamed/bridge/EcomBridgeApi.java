package to.be.renamed.bridge;

import to.be.renamed.bridge.client.BridgeRequest;
import to.be.renamed.bridge.client.Json;
import to.be.renamed.bridge.client.UnirestConnector;
import to.be.renamed.bridge.client.UnirestInterceptor;
import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import to.be.renamed.module.projectconfig.model.BridgeConfig;

import de.espirit.common.tools.Strings;

import org.jetbrains.annotations.Nullable;

import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static to.be.renamed.bridge.client.PagedBridgeRequest.pagedBridgeRequest;
import static java.util.stream.Collectors.toList;

public class EcomBridgeApi {

    private final Map<String, EcomCategory> categories = new LinkedHashMap<>();

    private final UnirestConnector unirestConnector;

    private EcomBridgeApi(BridgeConfig bridgeConfig) {
        unirestConnector = UnirestConnector.create(bridgeConfig);
        unirestConnector.interceptWith(new UnirestInterceptor());
    }

    protected static EcomBridgeApi create(BridgeConfig bridgeConfig) {
        return new EcomBridgeApi(bridgeConfig);
    }

    protected void configure(BridgeConfig bridgeConfig) {
        unirestConnector.configureApiClient(bridgeConfig);
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
        String endpointRoute = "/categories/ids/{categoryIds}";

        return BridgeRequest.bridgeRequest(
                unirestConnector.getCachingHttpClient().get(endpointRoute)
                    .routeParam("categoryIds", Strings.implode(categoryIds, ","))
                    .queryString("lang", lang))
            .getItems()
            .stream().map(category -> new EcomCategory(new Json(category)))
            .filter(EcomCategory::isValid)
            .collect(toList());
    }

    public List<EcomCategory> findCategories(@Nullable String parentId, @Nullable String lang) {
        GetRequest baseRequest = unirestConnector.getCachingHttpClient().get("/categories/");

        if (parentId != null) {
            baseRequest.queryString("parentId", parentId);
        }
        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }
        return pagedBridgeRequest(baseRequest, unirestConnector.getCachingHttpClient())
            .getItems()
            .stream()
            .map(category -> new EcomCategory(new Json(category)))
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
            final GetRequest baseRequest = unirestConnector.getCachingHttpClient().get("/categories/tree");

            baseRequest.queryString("lang", lang);

            flattenCategories(BridgeRequest.bridgeRequest(baseRequest)
                                  .getItems()
                                  .stream().map(category -> new EcomCategory(new Json(category)))
                                  .filter(EcomCategory::isValid)
                                  .collect(toList()), categories);
        }
        return categories;
    }

    public boolean hasCategoryTree() {
        final int status = BridgeRequest.bridgeRequest(unirestConnector.getHttpClientWithoutCache().head("/categories/tree")).perform();
        return isStatusOk(status);
    }

    public List<EcomProduct> getProducts(Collection<String> productIds, @Nullable String lang) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        String endpointRoute = "/products/ids/{productIds}";

        final GetRequest baseRequest = unirestConnector.getCachingHttpClient().get(endpointRoute);
        baseRequest.routeParam("productIds", Strings.implode(productIds, ","));

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return BridgeRequest.bridgeRequest(baseRequest)
            .getItems()
            .stream().map(product -> new EcomProduct(new Json(product)))
            .filter(EcomProduct::isValid)
            .collect(toList());
    }

    public List<EcomProduct> findProducts(@Nullable String q, @Nullable String categoryId, @Nullable String lang) {
        final GetRequest baseRequest = unirestConnector.getCachingHttpClient().get("/products/");

        if (q != null) {
            baseRequest.queryString("q", q);
        }
        if (categoryId != null) {
            baseRequest.queryString("categoryId", categoryId);
        }
        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return pagedBridgeRequest(baseRequest, unirestConnector.getCachingHttpClient())
            .getItems()
            .stream().map(product -> new EcomProduct(new Json(product)))
            .filter(EcomProduct::isValid)
            .collect(toList());
    }

    public final boolean hasContent() {
        final int status = BridgeRequest.bridgeRequest(unirestConnector.getHttpClientWithoutCache().head("/content")).perform();
        return isStatusOk(status);
    }

    public List<EcomContent> getContent(Collection<String> contentIds, @Nullable String lang) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }

        final GetRequest baseRequest = unirestConnector.getCachingHttpClient().get("/content/ids/{contentIds}");
        baseRequest.routeParam("contentIds", Strings.implode(contentIds, ","));

        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }

        return BridgeRequest.bridgeRequest(baseRequest)
            .getItems()
            .stream().map(content -> new EcomContent(new Json(content)))
            .filter(EcomContent::isValid)
            .collect(toList());
    }


    public List<EcomContent> findContent(@Nullable String q, @Nullable String lang) {
        final GetRequest baseRequest = unirestConnector.getCachingHttpClient().get("/content/");

        if (q != null) {
            baseRequest.queryString("q", q);
        }
        if (lang != null) {
            baseRequest.queryString("lang", lang);
        }
        return pagedBridgeRequest(baseRequest, unirestConnector.getCachingHttpClient())
            .getItems()
            .stream().map(content -> new EcomContent(new Json(content)))
            .filter(EcomContent::isValid)
            .collect(toList());
    }

    public String createContent(EcomElementDTO data) {
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClientWithoutCache().post("/content/");

        return BridgeRequest.bridgeRequest(baseRequest
                                               .body(BridgeUtilities.toJSONObject(data.getJsonModel()))
                                               .header("Content-Type", "application/json"))
            .getItem()
            .get("id");
    }

    public void updateContent(String contentId, EcomElementDTO data) {
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClientWithoutCache().put("/content/{contentId}");

        BridgeRequest.bridgeRequest(baseRequest
                                        .header("Content-Type", "application/json")
                                        .body(BridgeUtilities.toJSONObject(data.getJsonModel()))
                                        .routeParam("contentId", contentId))
            .perform();
    }

    public void deleteContent(String contentId) {
        final HttpRequestWithBody baseRequest = unirestConnector.getHttpClientWithoutCache().delete("/content/{contentId}");

        baseRequest.routeParam("contentId", contentId);

        BridgeRequest.bridgeRequest(baseRequest)
            .perform();
    }

    public String getStoreFrontUrl(EcomId ecomId) {
        if (ecomId.isValid()) {
            final GetRequest baseRequest = unirestConnector.getCachingHttpClient().get("/storefront-url");

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
                unirestConnector.getCachingHttpClient().get("/lookup-url")
                    .queryString("url", storeFrontUrl))
                               .getItem());
    }

    public static BridgeTestResult testConnection(UnirestConnector connector, TestConnectionRequest params) {
        return new TestConnectionJob(connector).test(params);
    }

    public BridgeTestResult testConnection(BridgeConfig bridgeConfig, TestConnectionRequest params) {
        // create a new http client to not override the config of the current one
        return testConnection(UnirestConnector.create(bridgeConfig), params);
    }

    /**
     * Returns true if the given status code is between 200 and 300
     *
     * @param statusCode Input to evaluate
     * @return Result
     */
    private static boolean isStatusOk(int statusCode) {
        return statusCode >= HttpStatus.OK && statusCode < HttpStatus.MULTIPLE_CHOICE;
    }
}
