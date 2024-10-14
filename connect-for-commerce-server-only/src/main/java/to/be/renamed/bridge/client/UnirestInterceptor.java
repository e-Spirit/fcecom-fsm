package to.be.renamed.bridge.client;

import de.espirit.common.base.Logging;

import kong.unirest.Config;
import kong.unirest.FailedResponse;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.Interceptor;

import static java.lang.String.format;

public class UnirestInterceptor implements Interceptor {

    public UnirestInterceptor() {
    }

    @Override
    public void onRequest(HttpRequest<?> request, Config config) {
        Logging.logDebug(format("Performing '%s' request on '%s'", request.getHttpMethod(), request.getUrl()), getClass());
    }

    @Override
    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
        String msg = format("Received response with status '%s - %s'", response.getStatus(), response.getStatusText());
        if (!response.isSuccess()) {
            Logging.logError(msg, getClass());
        } else {
            Logging.logDebug(msg, getClass());
        }

        final StringBuilder logMessage = new StringBuilder(format("%s %s - %s %s",
                                                                  request.getHttpMethod().name(), request.getUrl(),
                                                                  response.getStatus(), response.getStatusText()));

        response.getParsingError().ifPresentOrElse(parsingError -> {
            Logging.logError(logMessage + "\n\t" + parsingError.getMessage(), parsingError, getClass());
        }, () -> { // no parsing error
            Logging.logDebug(logMessage.toString(), getClass());
        });
    }

    @Override
    public HttpResponse<?> onFail(Exception exception, HttpRequestSummary request, Config config) {
        Logging.logError(format("%s %s%n\t%s",
                                request.getHttpMethod().name(), request.getUrl(),
                                exception.getMessage()), exception, getClass());

        return new FailedResponse<>(exception);
    }
}