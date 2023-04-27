package to.be.renamed.bridge.client;

import de.espirit.common.base.Logging;
import kong.unirest.Config;
import kong.unirest.FailedResponse;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.Interceptor;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;

import static java.lang.String.format;

public class UnirestInterceptor implements Interceptor {
    private final StringBuilder summary;
    private String lastError;

    public UnirestInterceptor() {
        summary = null;
    }

    /**
     * Initializes an Interceptor for kong Unirest client for logging.
     *
     * @param summary used for logging
     */
    public UnirestInterceptor(StringBuilder summary) {
        this.summary = summary;
    }

    @Override
    public void onRequest(HttpRequest<?> request, Config config) {
        Logging.logDebug(String.format("Performing '%s' request on '%s'", request.getHttpMethod(), request.getUrl()), getClass());
    }

    @Override
    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {

        String msg = String.format("Received response with status '%s - %s'", response.getStatus(), response.getStatusText());
        if (!response.isSuccess()) {
            Logging.logError(msg, getClass());
        } else {
            Logging.logDebug(msg, getClass());
        }

        final StringBuilder logMessage = new StringBuilder(format("%s %s - %s %s",
            request.getHttpMethod().name(), request.getUrl(),
            response.getStatus(), response.getStatusText()));

        response.getParsingError().ifPresentOrElse(parsingError -> {
            logMessage
                .append(" -> ")
                .append(parsingError.getOriginalBody());

            Logging.logError(logMessage + "\n\t" + parsingError.getMessage(), parsingError, getClass());

            this.lastError = logMessage + "\n\t" + parsingError;
        }, () -> { // no parsing error
            Object body = response.getBody();

            if (body instanceof JsonNode) {
                JsonNode jsonNode = (JsonNode) body;

                if (!jsonNode.isArray() && jsonNode.getObject().has("error")) {
                    JSONArray error = jsonNode.getObject().getJSONArray("error");
                    if (error.length() > 0) logMessage.append("\n\t").append(error.toString(4));
                }
            }

            Logging.logDebug(logMessage.toString(), getClass());
            this.lastError = logMessage.toString();
        });

        if (summary != null) {
            this.summary.append(this.lastError).append("\n");
        }
    }

    @Override
    public HttpResponse<?> onFail(Exception exception, HttpRequestSummary request, Config config) {
        Logging.logError(format("%s %s%n\t%s",
            request.getHttpMethod().name(), request.getUrl(),
            exception.getMessage()), exception, getClass());

        if (this.summary != null) {
            this.summary.append(format("%s %s%n\t%s%n",
                request.getHttpMethod().name(),
                request.getUrl(),
                exception.getMessage()));
        }

        return new FailedResponse<>(exception);
    }
}
