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
    private final String bridgePassword ;

    /**
     * Required by Gson.
     * Creates an empty bridge configuration.
     */
    public BridgeConfig() {
        this("", "", "");
    }

    /**
     * Creates a bridge configuration.
     * @param bridgeApiUrl URL of the bridge
     * @param bridgeUsername Username for bridge authentication
     * @param bridgePassword Password for bridge authentication
     */
    public BridgeConfig(String bridgeApiUrl, String bridgeUsername, String bridgePassword) {
        this.bridgeApiUrl = ConfigUtils.removeTrailingSlash(Objects.requireNonNullElse(bridgeApiUrl, ""));
        this.bridgeUsername = Objects.requireNonNullElse(bridgeUsername, "");
        this.bridgePassword = Objects.requireNonNullElse(bridgePassword, "");
    }

    /**
     * Creates a bridge configuration from values.
     * Needed for char[] input as provided by BridgeConfigurationPanel.
     * @param bridgeApiUrl Url of the bridge
     * @param bridgeUsername Username for bridge authentication
     * @param bridgePassword Password for bridge authentication
     * @return A bridge config
     */
    public static BridgeConfig fromValues(final String bridgeApiUrl, final String bridgeUsername, final char[] bridgePassword) {
        return new BridgeConfig(bridgeApiUrl, bridgeUsername, String.valueOf(bridgePassword));
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

    @Override
    public String toString() {
        return format("{bridgeApiUrl: %s, bridgeUsername: %s}", bridgeApiUrl, bridgeUsername);
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
                Objects.equals(getBridgePassword(), that.getBridgePassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBridgeApiUrl(), getBridgeUsername(), getBridgePassword());
    }
}
