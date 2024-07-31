package to.be.renamed.bridge.client;

import to.be.renamed.module.projectconfig.model.CacheConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import de.espirit.common.base.Logging;
import kong.unirest.HttpResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static de.espirit.common.base.Logging.logError;
import static java.util.concurrent.TimeUnit.SECONDS;

public class GuavaCache implements kong.unirest.Cache {

    private final Cache<Key, HttpResponse<?>> regular;
    private final Cache<Key, CompletableFuture<?>> async;

    /**
     * Cache for BridgeRequests, configured through Project App Config.
     * @param cacheConfig Configuration for Cache Size and Age
     */
    public GuavaCache(CacheConfig cacheConfig) {
        final int cacheAge = cacheConfig.getCacheAge();
        final int cacheSize = cacheConfig.getCacheSize();

        this.regular = CacheBuilder.newBuilder().maximumSize(cacheSize)
                .expireAfterWrite(cacheAge, SECONDS)
                .removalListener(this::log).build();

        this.async = CacheBuilder.newBuilder().maximumSize(cacheSize)
                .expireAfterWrite(cacheAge, SECONDS)
                .removalListener(this::log).build();
    }

    @Override
    public <T> HttpResponse<?> get(Key key, Supplier<HttpResponse<T>> fetcher) {
        try {
            return regular.get(key, fetcher::get);
        } catch (ExecutionException exception) {
            logError(exception.getMessage(), exception, getClass());
            return null;
        }
    }

    @Override
    public <T> CompletableFuture<?> getAsync(Key key, Supplier<CompletableFuture<HttpResponse<T>>> fetcher) {
        try {
            return async.get(key, fetcher::get);
        } catch (ExecutionException exception) {
            logError(exception.getMessage(), exception, getClass());
            return null;
        }
    }

    public void invalidate() {
        regular.invalidateAll();
        async.invalidateAll();
    }

    void log(final RemovalNotification<Object, Object> notification) {
        switch (notification.getCause()) {
            case SIZE -> Logging.logDebug("Cleared Cache Element due to Size Limitations", getClass());
            case EXPIRED -> Logging.logDebug("Cleared Cache Element due to Expiration", getClass());
            default -> Logging.logDebug("Cleared Cache Element (%s)".formatted(notification.getCause()), getClass());
        }
    }
}
