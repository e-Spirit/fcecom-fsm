package to.be.renamed.module.projectconfig.model;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;

/**
 * Representation of the general configuration.
 */
public class GeneralConfig implements Serializable {

    private static final long serialVersionUID = -3503341880886071076L;
    private final String ccExtensionsUrl;
    private final Boolean useCCExtensions;
    private final Boolean disableBridgePageCreation;

    /**
     * Required by Gson.
     * Creates a general configuration with default values.
     * (ccExtensionsUrl = "", useCCExtensions = false, disableBridgePageCreation = false)
     */
    public GeneralConfig() {
        this("", false, false);
    }

    /**
     * Creates a general configuration
     *
     * @param ccExtensionsUrl           URL to load the ContentCreator Extensions from
     * @param useCCExtensions           Whether to use ContentCreator Extensions
     * @param disableBridgePageCreation Flag to disable bride page creation
     */
    public GeneralConfig(String ccExtensionsUrl, Boolean useCCExtensions, Boolean disableBridgePageCreation) {
        this.ccExtensionsUrl = ConfigUtils.removeTrailingSlash(Objects.requireNonNullElse(ccExtensionsUrl, ""));
        this.useCCExtensions = Objects.requireNonNullElse(useCCExtensions, false);
        this.disableBridgePageCreation = Objects.requireNonNullElse(disableBridgePageCreation, false);
    }

    /**
     * Creates a general config from strings, uses false if strings are not parseable.
     * Needed when reading values from file.
     *
     * @param ccExtensionsUrlString           URL to load the ContentCreator Extensions from
     * @param useCCExtensionsString           Whether to use ContentCreator Extensions
     * @param disableBridgePageCreationString Flag to disable bride page creation
     * @return A general configuration
     */
    public static GeneralConfig fromStrings(String ccExtensionsUrlString, String useCCExtensionsString, String disableBridgePageCreationString) {
        return new GeneralConfig(ccExtensionsUrlString, parseBoolean(useCCExtensionsString), parseBoolean(disableBridgePageCreationString));
    }

    public String getCcExtensionsUrl() {
        return ccExtensionsUrl;
    }

    public Boolean useCCExtensions() {
        return useCCExtensions;
    }

    public Boolean disableBridgePageCreation() {
        return disableBridgePageCreation;
    }

    @Override
    public String toString() {
        return format("{ccExtensionsUrl: %s, useCCExtensions: %s, disableBridgePageCreation: %s}", ccExtensionsUrl, useCCExtensions,
                      disableBridgePageCreation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GeneralConfig that = (GeneralConfig) o;
        return Objects.equals(getCcExtensionsUrl(), that.getCcExtensionsUrl()) &&
               Objects.equals(useCCExtensions, that.useCCExtensions) &&
               Objects.equals(disableBridgePageCreation, that.disableBridgePageCreation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCcExtensionsUrl(), useCCExtensions, disableBridgePageCreation);
    }
}
