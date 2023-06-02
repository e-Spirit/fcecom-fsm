package to.be.renamed.bridge.client;

import com.google.common.cache.CacheBuilder;
import kong.unirest.HttpResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static de.espirit.common.base.Logging.logError;

public class Cache implements kong.unirest.Cache {

    private final com.google.common.cache.Cache<Key, HttpResponse<?>> regular = CacheBuilder.newBuilder().build();
    private final com.google.common.cache.Cache<Key, CompletableFuture<?>> async = CacheBuilder.newBuilder().build();

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
}
