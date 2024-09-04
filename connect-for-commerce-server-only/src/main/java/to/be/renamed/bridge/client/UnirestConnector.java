package to.be.renamed.bridge.client;

import to.be.renamed.module.projectconfig.model.BridgeConfig;

import kong.unirest.Cache;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

/**
 * Unirest config utils
 */
public final class UnirestConnector {

    private final UnirestInstance cachedUnirest;
    private final UnirestInstance unirestWithoutCache;
    private GuavaCache cache;

    public static final String PROJECT_ID_HEADER = "FS-Project-UUID";

    UnirestConnector(BridgeConfig bridgeConfig) {
        cachedUnirest = Unirest.spawnInstance();
        unirestWithoutCache = Unirest.spawnInstance();
        configureApiClient(bridgeConfig);
    }

    /**
     * Creates an instance of the UnirestConnector and configures the http client with the values of the given BridgeConfig.
     *
     * @param bridgeConfig The config for the http client.
     * @return An instance of the UnirestConnector with the configured http client.
     */
    public static UnirestConnector create(BridgeConfig bridgeConfig) {
        return new UnirestConnector(bridgeConfig);
    }

    /**
     * Configures the http client with the given BridgeConfig, adds a default header and an interceptor.
     *
     * @param bridgeConfig The config for the http client.
     */
    public void configureApiClient(BridgeConfig bridgeConfig) {
        if (cachedUnirest.isRunning()) {
            cachedUnirest.shutDown();
        }
        if (unirestWithoutCache.isRunning()) {
            unirestWithoutCache.shutDown();
        }

        this.cache = new GuavaCache(bridgeConfig.getCacheConfig());

        cachedUnirest.config()
            .defaultBaseUrl(bridgeConfig.getBridgeApiUrl())
            .setDefaultBasicAuth(bridgeConfig.getBridgeUsername(), bridgeConfig.getBridgePassword())
            .setDefaultHeader("Accept", "application/json")
            .addDefaultHeader(PROJECT_ID_HEADER, String.valueOf(bridgeConfig.getProjectUuid()))
            .cacheResponses(Cache.builder().backingCache(getCache()));

        unirestWithoutCache.config()
            .defaultBaseUrl(bridgeConfig.getBridgeApiUrl())
            .setDefaultBasicAuth(bridgeConfig.getBridgeUsername(), bridgeConfig.getBridgePassword())
            .setDefaultHeader("Accept", "application/json")
            .addDefaultHeader(PROJECT_ID_HEADER, String.valueOf(bridgeConfig.getProjectUuid()));
    }

    /**
     * Public method to override the interceptor.
     * Needed to have an empty summary of the request results.
     *
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
