package to.be.renamed.module.projectconfig.model;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Representation of the project app configuration.
 * Bundles general, bridge and report configuration.
 */
public class ProjectAppConfiguration implements Serializable {

    private static final long serialVersionUID = 7625515744968503386L;
    private final GeneralConfig generalConfig;
    private final BridgeConfig bridgeConfig;
    private final ReportConfig reportConfig;

    @SuppressWarnings("unused") // Required by Gson
    private ProjectAppConfiguration() {
        this(null, null, null);
    }

    /**
     * Creates a project app configuration.
     *
     * @param generalConfig General configuration
     * @param bridgeConfig  Bridge configuration
     * @param reportConfig  Report configuration
     */
    public ProjectAppConfiguration(GeneralConfig generalConfig, BridgeConfig bridgeConfig, ReportConfig reportConfig) {
        this.generalConfig = Objects.requireNonNullElse(generalConfig, new GeneralConfig());
        this.bridgeConfig = Objects.requireNonNullElse(bridgeConfig, new BridgeConfig());
        this.reportConfig = Objects.requireNonNullElse(reportConfig, new ReportConfig());
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public BridgeConfig getBridgeConfig() {
        return bridgeConfig;
    }

    public ReportConfig getReportConfig() {
        return reportConfig;
    }

    @Override
    public String toString() {
        return format("{generalConfig: [%s], bridgeConfig: [%s], reportConfig: [%s]}", generalConfig, bridgeConfig, reportConfig);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectAppConfiguration that = (ProjectAppConfiguration) o;
        return Objects.equals(getGeneralConfig(), that.getGeneralConfig()) &&
               Objects.equals(getBridgeConfig(), that.getBridgeConfig()) &&
               Objects.equals(getReportConfig(), that.getReportConfig());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeneralConfig(), getBridgeConfig(), getReportConfig());
    }
}
