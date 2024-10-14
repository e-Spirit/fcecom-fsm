package to.be.renamed.module.projectconfig.model;

import de.espirit.common.base.Logging;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

/**
 * Representation of the cache configuration.
 */
public class CacheConfig implements Serializable {

    private static final long serialVersionUID = 969025740620384332L;
    private static final int DEFAULT_CACHE_SIZE = 100;
    private static final int DEFAULT_CACHE_AGE = 5 * 60;
    private static final int MAX_DURATION = parseDuration("99:23:59:59");
    private final int cacheSize;
    private final int cacheAge;

    /**
     * Required by Gson. Creates a cache configuration with default values. (cacheSize = 1 ,
     * cacheAge = 5 minutes)
     */
    public CacheConfig() {
        this(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_AGE);
    }

    /**
     * Creates a cache configuration.
     *
     * @param cacheSize Number of HTTP Responses to keep alive in the cache before starting to clear the oldest elements.
     * @param cacheAge  Maximum lifetime of Cache entries in seconds.
     */
    public CacheConfig(int cacheSize, int cacheAge) {
        this.cacheSize = cacheSize;
        this.cacheAge = cacheAge;
    }

    /**
     * Creates a cache configuration from strings, uses default values when input is not parseable to
     * int. (cacheSize = 100, cacheAge = 5 * 60 seconds) Needed when reading values from file.
     *
     * @param cacheSize Number of HTTP Responses to keep alive in the cache before starting to clear the oldest elements.
     * @param cacheAge  Maximum lifetime of Cache entries in seconds.
     * @return A cache configuration
     */
    public static CacheConfig fromStrings(String cacheSize, String cacheAge) {
        int actualCacheSize = DEFAULT_CACHE_SIZE;
        int actualCacheAge = DEFAULT_CACHE_AGE;

        try {
            actualCacheSize = parseInt(cacheSize);
        } catch (NumberFormatException nfe) {
            Logging.logWarning(
                "Unable to parse configured cache size. Using default value '"
                + DEFAULT_CACHE_SIZE
                + "'.",
                nfe, CacheConfig.class);
        }

        if (cacheAge == null) {
            return new CacheConfig(actualCacheSize, actualCacheAge);
        }

        try {
            actualCacheAge = withinBoundaries(parseDuration(cacheAge));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
            Logging.logWarning(
                "Unable to parse configured cache age. Using default value '%d'.".formatted(DEFAULT_CACHE_AGE),
                exception, CacheConfig.class);
        }

        return new CacheConfig(actualCacheSize, actualCacheAge);
    }

    private static int parseDuration(String cacheAge) {
        final String[] split = cacheAge.split(":");

        final int seconds = Integer.parseInt(split[3]);
        final int minutes = Integer.parseInt(split[2]) * 60;
        final int hours = Integer.parseInt(split[1]) * 60 * 60;
        final int days = Integer.parseInt(split[0]) * 24 * 60 * 60;

        return seconds + minutes + hours + days;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getCacheAge() {
        return cacheAge;
    }

    public String getCacheSizeAsString() {
        return String.valueOf(cacheSize);
    }

    public String getCacheAgeAsString() {
        final Duration cacheAgeDuration = Duration.ofSeconds(withinBoundaries(cacheAge));
        return format("%02d:%02d:%02d:%02d",
                      cacheAgeDuration.toDaysPart(),
                      cacheAgeDuration.toHoursPart(),
                      cacheAgeDuration.toMinutesPart(),
                      cacheAgeDuration.toSecondsPart());
    }

    private static int withinBoundaries(int target) {
        return withinBoundaries(target, 0, MAX_DURATION);
    }

    private static int withinBoundaries(int target, int min, int max) {
        return Math.min(Math.max(target, min), max);
    }

    @Override
    public String toString() {
        return format("{cacheSize: %s, cacheAge: %s}", cacheSize, this.getCacheAgeAsString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CacheConfig that = (CacheConfig) o;
        return getCacheSize() == that.getCacheSize() && getCacheAge() == that.getCacheAge();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCacheSize(), getCacheAge());
    }
}
