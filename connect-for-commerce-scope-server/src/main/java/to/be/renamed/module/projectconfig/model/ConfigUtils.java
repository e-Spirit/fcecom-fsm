package to.be.renamed.module.projectconfig.model;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Utility class for configuration
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    /**
     * Removes a trailing slash in present
     *
     * @param input Input string
     * @return The input without the trailing slash
     */
    public static String removeTrailingSlash(@Nullable String input) {
        return Objects.requireNonNullElse(input, "").replaceFirst("/*$", "");
    }
}
