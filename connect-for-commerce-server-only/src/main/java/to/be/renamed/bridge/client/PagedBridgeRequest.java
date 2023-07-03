package to.be.renamed.bridge.client;

import com.google.gson.JsonElement;
import kong.unirest.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static to.be.renamed.bridge.BridgeUtilities.toJsonElement;

/**
 * Bridge Request for loading all pages of a specific GET endpoint
 */
public class PagedBridgeRequest extends BridgeRequest {
    private final UnirestInstance httpClient;
    PagedBridgeRequest(HttpRequest<?> baseRequest, UnirestInstance httpClient) {
        super(baseRequest);
        this.httpClient = httpClient;
    }

    /**
     *
     * @param baseRequest Base Request with URL and query params
     * @return Instance of PagedBridgeRequest
     */
    public static PagedBridgeRequest pagedBridgeRequest(HttpRequest<?> baseRequest, UnirestInstance httpClient) {
        return new PagedBridgeRequest(baseRequest, httpClient);
    }

    @Override
    public List<JsonElement> getItems() {
        final List<JsonElement> items = new ArrayList<>();
        paginatedRequest()
            .getBodies()
            // flattens page structure
            .forEach(page -> page.getArray().forEach(element -> items.add(toJsonElement(element))));
        return items;
    }

    private PagedList<JsonNode> paginatedRequest() {
        PagedList<JsonNode> all = new PagedList<>();
        boolean hasNext;
        AtomicInteger page = new AtomicInteger(1);
        do {
            // create new request
            GetRequest request = httpClient.get(baseRequest.getUrl());
            // add query string
            request.queryString("page", page.getAndIncrement());
            // make Request
            HttpResponse<JsonNode> response = request.asJson();
            // add to list
            all.add(response);
            // check next condition
            hasNext = Objects.equals(response.getHeaders().getFirst("X-HasNext"), "true");
        } while (hasNext);
        return all;
    }
}
