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
    private final GuavaCache cache;

    private final UnirestInstance cachedUnirest;
    private final UnirestInstance unirestWithoutCache;

    UnirestConnector(BridgeConfig bridgeConfig, GuavaCache cache) {
        cachedUnirest = Unirest.spawnInstance();
        unirestWithoutCache = Unirest.spawnInstance();
        this.cache = cache;
        configureApiClient(bridgeConfig);
    }

    UnirestConnector(BridgeConfig bridgeConfig) {
        this(bridgeConfig, new GuavaCache());
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
     * Creates an instance of the UnirestConnector and configures the http client with the values of the given BridgeConfig.
     * @param bridgeConfig The config for the http client.
     * @param cache The instance of GuavaCache to use for requests made by the cachedHttpClient.
     * @return An instance of the UnirestConnector with the configured http client.
     */
    public static UnirestConnector create(BridgeConfig bridgeConfig, GuavaCache cache) {
        return new UnirestConnector(bridgeConfig, cache);
    }

    /**
     * Configures the http client with the given BridgeConfig, adds a default header and an interceptor.
     * @param bridgeConfig The config for the http client.
     */
    public void configureApiClient(BridgeConfig bridgeConfig) {
        if (cachedUnirest.isRunning()) {
            cachedUnirest.shutDown();
        }
        if (unirestWithoutCache.isRunning()) {
            unirestWithoutCache.shutDown();
        }

        cachedUnirest.config()
                .defaultBaseUrl(bridgeConfig.getBridgeApiUrl())
                .setDefaultBasicAuth(bridgeConfig.getBridgeUsername(), bridgeConfig.getBridgePassword())
                .setDefaultHeader("Accept", "application/json")
                .cacheResponses(kong.unirest.Cache.builder()
                        .depth(CACHE_DEPTH).maxAge(CACHE_MAX_AGE, MINUTES).backingCache(getCache()));

        unirestWithoutCache.config()
                .defaultBaseUrl(bridgeConfig.getBridgeApiUrl())
                .setDefaultBasicAuth(bridgeConfig.getBridgeUsername(), bridgeConfig.getBridgePassword())
                .setDefaultHeader("Accept", "application/json");
    }

    /**
     * Public method to override the interceptor.
     * Needed to have an empty summary of the request results.
     * @param interceptor The intercept which overrides the existent one.
     */
    public void interceptWith(UnirestInterceptor interceptor) {
        cachedUnirest.config().interceptor(interceptor);
        unirestWithoutCache.config().interceptor(interceptor);
    }

    /**
     * Shuts down the http client.
     */
    public void shutDown() {
        if (cachedUnirest.isRunning()) {
            cachedUnirest.shutDown();
        }
        if (unirestWithoutCache.isRunning()) {
            unirestWithoutCache.shutDown();
        }
    }

    public UnirestInstance getCachingHttpClient() {
        return cachedUnirest;
    }

    public UnirestInstance getHttpClientWithoutCache() {
        return unirestWithoutCache;
    }

    public GuavaCache getCache() {
        return cache;
    }
}
