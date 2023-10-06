package to.be.renamed.bridge;

import to.be.renamed.bridge.client.TestConnectionInterceptor;
import to.be.renamed.bridge.client.UnirestConnector;
import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import kong.unirest.UnirestInstance;

import static to.be.renamed.bridge.client.BridgeRequest.bridgeRequest;

public class TestConnectionJob {
    private final UnirestInstance httpClient;
    private final TestConnectionInterceptor interceptor;

    public TestConnectionJob(UnirestConnector connector) {
        this.httpClient = connector.getHttpClient();
        this.interceptor = new TestConnectionInterceptor();

        connector.interceptWith(interceptor);
    }

    public BridgeTestResult test(TestConnectionRequest params) {
        bridgeRequest(this.httpClient.request(params.httpMethod(), params.url())).perform();
        return interceptor.getResult()
                .setDeprecated(params.deprecated());
    }
}
