package to.be.renamed.bridge.client;

import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import kong.unirest.Config;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import static to.be.renamed.bridge.client.UnirestConnector.PROJECT_ID_HEADER;
import static de.espirit.common.tools.Strings.notEmpty;

public class TestConnectionInterceptor extends UnirestInterceptor {

    private BridgeTestResult result;

    @Override
    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
        super.onResponse(response, request, config);

        this.result = new BridgeTestResult()
                .setRequestData(request.getHttpMethod().name(), request.getUrl(),
                                config.getDefaultHeaders().get(PROJECT_ID_HEADER)
                                    .stream().findFirst().orElse(null))
                .setResponseData(response.getStatus(), response.getStatusText());

        response.getParsingError().ifPresentOrElse(parsingError -> {
            this.result.setParsingError(parsingError.getOriginalBody());
        }, () -> { // no parsing error
            Object body = response.getBody();

            if (body instanceof JsonNode) {
                JsonNode jsonNode = (JsonNode) body;
                if (!jsonNode.isArray() && jsonNode.getObject().has("error")) {
                    String error = jsonNode.getObject().getString("error");
                    if (notEmpty(error)) {
                        this.result.setErrorResponse(error);
                    }
                }
            }
        });
    }

    @Override
    public HttpResponse<?> onFail(Exception exception, HttpRequestSummary request, Config config) {
        this.result = new BridgeTestResult()
                .setRequestData(request.getHttpMethod().name(), request.getUrl(),
                                config.getDefaultHeaders().get(PROJECT_ID_HEADER).stream().findFirst().orElse(null))
                .setException(exception);

        return super.onFail(exception, request, config);
    }

    public BridgeTestResult getResult() {
        return this.result;
    }
}
