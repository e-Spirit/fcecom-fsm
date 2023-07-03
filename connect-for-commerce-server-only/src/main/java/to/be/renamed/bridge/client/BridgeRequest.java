package to.be.renamed.bridge.client;

import to.be.renamed.error.CreatePageException;
import to.be.renamed.error.ErrorExtractor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpMethod;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;


import java.util.ArrayList;
import java.util.List;

import static to.be.renamed.bridge.BridgeUtilities.toJsonElement;
import static java.util.Collections.emptyList;

public class BridgeRequest {
    final HttpRequest<?> baseRequest;

    BridgeRequest(HttpRequest<?> baseRequest) {
        if (baseRequest == null) throw new IllegalArgumentException("baseRequest has to be set. Found null value");
        this.baseRequest = baseRequest;
    }

    /**
     * Simple static function to make creating an instance
     *  of a Bridge request very convenient.
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
            return handleBridgeError(response);
        }

        JsonNode jsonResponse = response.getBody();

        // everything ok
        if (jsonResponse != null && !jsonResponse.isArray()) {
            JsonElement jsonElement = toJsonElement(jsonResponse.getObject());
            return new Json((JsonObject) jsonElement);
        }

        return new Json();
    }

    private Json handleBridgeError(HttpResponse<JsonNode> response) {
        if ((baseRequest.getHttpMethod().equals(HttpMethod.POST) ||
                baseRequest.getHttpMethod().equals(HttpMethod.PUT)) &&
                response.getStatus() == HttpStatus.BAD_REQUEST) {
            JsonNode body = response.getBody();
            throw new CreatePageException("Error creating page:", ErrorExtractor.extractBodyValidationErrors(toJsonElement(body.getObject())), null);
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

        if (!response.isSuccess()) return emptyList();
        if (!response.getBody().isArray()) return emptyList();

        final JsonNode body = response.getBody();

        List<JsonElement> items = new ArrayList<>();
        body.getArray().forEach(item -> items.add(toJsonElement(item)));
        return items;
    }
}
