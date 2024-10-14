package to.be.renamed.bridge.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.espirit.common.base.Logging;

import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;


import java.util.ArrayList;
import java.util.List;

import static to.be.renamed.bridge.BridgeUtilities.toJsonElement;
import static java.util.Collections.emptyList;

public class BridgeRequest {

    private int total;
    final HttpRequest<?> baseRequest;

    BridgeRequest(HttpRequest<?> baseRequest) {
        if (baseRequest == null) {
            throw new IllegalArgumentException("baseRequest has to be set. Found null value");
        }
        this.baseRequest = baseRequest;
    }

    /**
     * Simple static function to make creating an instance
     * of a Bridge request very convenient.
     *
     * @param baseRequest Prepared {@link HttpRequest} with all necessary
     *                    information for sending it
     * @return {@link BridgeRequest} with a variety of possible request formats as options
     */
    public static BridgeRequest bridgeRequest(HttpRequest<?> baseRequest) {
        return new BridgeRequest(baseRequest);
    }

    /**
     * Performs the request, expects an empty or non-existent body
     * and returns the status code of the response.
     *
     * @return status code of response
     */
    public int perform() {
        return baseRequest.asEmpty().getStatus();
    }

    /**
     * Gets a single item as {@link Json}.
     * Expects the response body to be a JSON object.
     *
     * @return JSON Object from response body
     */
    public Json getItem() {
        final HttpResponse<JsonNode> response = baseRequest.asJson();
        if (!response.isSuccess()) {
            BridgeErrorHandling.handleBridgeError(response);
        }

        JsonNode jsonResponse = response.getBody();

        // everything ok
        if (jsonResponse != null && !jsonResponse.isArray()) {
            JsonElement jsonElement = toJsonElement(jsonResponse.getObject());
            return new Json((JsonObject) jsonElement);
        }

        return new Json();
    }

    /**
     * Sends the base request and expects a JSON formatted array.
     * It converts the items to JsonElements and therefore returns a list of that type.
     *
     * @return List of JsonElements from the response bodies
     */
    public List<JsonElement> getItems() {
        final HttpResponse<JsonNode> response = baseRequest.asJson();

        if (!response.isSuccess()) {
            BridgeErrorHandling.handleBridgeError(response);
        }
        if (!response.getBody().isArray()) {
            return emptyList();
        }

        try {
            total = Integer.parseInt(response.getHeaders().getFirst("x-total"));
        } catch (final Exception e) {
            Logging.logInfo("Response doesn't provide x-total header.", e, getClass());
        }

        final JsonNode body = response.getBody();

        List<JsonElement> items = new ArrayList<>();
        body.getArray().forEach(item -> items.add(toJsonElement(item)));
        return items;
    }

    public int getTotal() {
        return total;
    }
}
