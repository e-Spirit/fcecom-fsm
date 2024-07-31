package to.be.renamed.module.projectconfig.model;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Representation of the bridge configuration.
 */
public class BridgeConfig implements Serializable {

    private static final long serialVersionUID = 5631576740668435849L;
    private final String bridgeApiUrl;
    private final String bridgeUsername;
    private final String bridgePassword;
    private final CacheConfig cacheConfig;

    /**
     * Required by Gson.
     * Creates an empty bridge configuration.
     */
    public BridgeConfig() {
        this("", "", "", new CacheConfig());
    }

    /**
     * Creates a bridge configuration.
     * @param bridgeApiUrl URL of the bridge
     * @param bridgeUsername Username for bridge authentication
     * @param bridgePassword Password for bridge authentication
     * @param cacheConfig Cache-specific configuration
     */
    public BridgeConfig(String bridgeApiUrl, String bridgeUsername, String bridgePassword, CacheConfig cacheConfig) {
        this.bridgeApiUrl = ConfigUtils.removeTrailingSlash(Objects.requireNonNullElse(bridgeApiUrl, ""));
        this.bridgeUsername = Objects.requireNonNullElse(bridgeUsername, "");
        this.bridgePassword = Objects.requireNonNullElse(bridgePassword, "");
        this.cacheConfig = Objects.requireNonNullElse(cacheConfig, new CacheConfig());
    }

    /**
     * Creates a bridge configuration from values.
     * Needed for char[] input as provided by BridgeConfigurationPanel.
     * @param bridgeApiUrl Url of the bridge
     * @param bridgeUsername Username for bridge authentication
     * @param bridgePassword Password for bridge authentication
     * @param cacheConfig Configuration for the internal cache
     * @return A bridge config
     */
    public static BridgeConfig fromValues(final String bridgeApiUrl, final String bridgeUsername, final char[] bridgePassword, final CacheConfig cacheConfig) {
        return new BridgeConfig(bridgeApiUrl, bridgeUsername, String.valueOf(bridgePassword), cacheConfig);
    }

    public String getBridgeApiUrl() {
        return bridgeApiUrl;
    }

    public String getBridgeUsername() {
        return bridgeUsername;
    }

    public String getBridgePassword() {
        return bridgePassword;
    }

    public CacheConfig getCacheConfig() {
        return cacheConfig;
    }

    @Override
    public String toString() {
        return format("{bridgeApiUrl: %s, bridgeUsername: %s, cacheConfig %s}", bridgeApiUrl, bridgeUsername, cacheConfig.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BridgeConfig that = (BridgeConfig) o;
        return Objects.equals(getBridgeApiUrl(), that.getBridgeApiUrl()) &&
                Objects.equals(getBridgeUsername(), that.getBridgeUsername()) &&
                Objects.equals(getBridgePassword(), that.getBridgePassword()) &&
                getCacheConfig().equals(that.getCacheConfig());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBridgeApiUrl(), getBridgeUsername(), getBridgePassword(), getCacheConfig().hashCode());
    }
}
