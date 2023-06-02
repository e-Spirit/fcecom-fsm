package to.be.renamed.bridge.client;

import to.be.renamed.module.projectconfig.model.BridgeConfig;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Unirest config utils
 */
public final class UnirestConnector {

    private static final int CACHE_DEPTH = 100;
    private static final int CACHE_MAX_AGE = 5;
    private Cache cache;

    private final UnirestInstance unirest;

    private UnirestConnector() {
        unirest = Unirest.spawnInstance();
    }

    private UnirestConnector(BridgeConfig bridgeConfig) {
        unirest = Unirest.spawnInstance();
        configureApiClient(bridgeConfig);
    }

    /**
     * Creates an instance of the UnirestConnector and configures the http client with the values of the given BridgeConfig.
     * @param bridgeConfig The config for the http client.
     * @return An instance of the UnirestConnector with the configured http client.
     */
    public static UnirestConnector create(BridgeConfig bridgeConfig) {
        return new UnirestConnector(bridgeConfig);
    }

    /**
     * Configures the http client with the given BridgeConfig, adds a default header and an interceptor.
     * @param bridgeConfig The config for the http client.
     */
    public void configureApiClient(BridgeConfig bridgeConfig) {
        if (unirest.isRunning()) {
            Unirest.config().reset();
        }

        unirest.config()
                .defaultBaseUrl(bridgeConfig.getBridgeApiUrl())
                .setDefaultBasicAuth(bridgeConfig.getBridgeUsername(), bridgeConfig.getBridgePassword())
                .setDefaultHeader("Accept", "application/json")
                .cacheResponses(kong.unirest.Cache.builder()
                        .depth(CACHE_DEPTH).maxAge(CACHE_MAX_AGE, MINUTES).backingCache(getCache()));

    }

    /**
     * Public method to override the interceptor.
     * Needed to have an empty summary of the request results.
     * @param interceptor The intercept which overrides the existent one.
     */
    public void interceptWith(UnirestInterceptor interceptor) {
        unirest.config().interceptor(interceptor);
    }

    /**
     * Shuts down the http client.
     */
    public void shutDown() {
        if (unirest.isRunning()) {
            unirest.shutDown();
        }
    }

    public UnirestInstance getHttpClient() {
        return unirest;
    }

    public Cache getCache() {
        return cache == null ? (cache = new Cache()) : cache;
    }
}
