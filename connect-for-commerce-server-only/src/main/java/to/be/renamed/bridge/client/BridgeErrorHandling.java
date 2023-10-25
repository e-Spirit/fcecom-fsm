package to.be.renamed.bridge.client;

import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.error.BridgeError;
import to.be.renamed.error.BridgeException;
import to.be.renamed.error.CreatePageException;
import to.be.renamed.error.ErrorCode;
import to.be.renamed.error.ErrorExtractor;

import kong.unirest.FailedResponse;
import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;

import java.util.List;

import static to.be.renamed.bridge.BridgeUtilities.toJsonElement;

/**
 * Provides static functions to handle error statuses from bridge responses.
 */
public final class BridgeErrorHandling {

    private BridgeErrorHandling() {
    }

    /**
     * Checks the given http response and throws the regarding exception with message and error code.
     *
     * @param response The http response of the bridge request
     */
    public static void handleBridgeError(HttpResponse<JsonNode> response) {
        if (response instanceof FailedResponse) {
            throw new BridgeConnectionException("Connection refused", ErrorCode.CANNOT_CONNECT_TO_BRIDGE.get());
        }
        switch (response.getStatus()) {
            case HttpStatus.BAD_REQUEST:
                handleBadRequest(response);
                break;
            case HttpStatus.UNAUTHORIZED:
                throw new BridgeConnectionException("Unauthorized", ErrorCode.BRIDGE_AUTH_ERROR.get());
            case HttpStatus.REQUEST_TIMEOUT:
                throw new BridgeConnectionException("Request timeout", ErrorCode.BRIDGE_REQUEST_TIMEOUT.get());
            case HttpStatus.INTERNAL_SERVER_ERROR:
                throw new BridgeException("Bridge server error. Please check the bridge logs.", ErrorCode.BRIDGE_SERVER_ERROR.get());
            default:
                throw new BridgeException("Unknown error", ErrorCode.UNKNOWN.get());
        }
    }

    private static void handleBadRequest(HttpResponse<JsonNode> response) {
        JsonNode body = response.getBody();
        List<BridgeError> bridgeErrors = ErrorExtractor.extractBodyValidationErrors(toJsonElement(body.getObject()));
        if (!bridgeErrors.isEmpty()) {
            throw new CreatePageException("Error creating page:", bridgeErrors, null);
        } else {
            throw new BridgeException("Unknown Error", ErrorCode.UNKNOWN.get());
        }
    }
}
