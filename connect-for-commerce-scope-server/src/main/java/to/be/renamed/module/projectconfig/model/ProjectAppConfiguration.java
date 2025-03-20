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
    private final FieldsConfig fieldsConfig;
    private final JwtConfig jwtConfig;

    @SuppressWarnings("unused") // Required by Gson
    private ProjectAppConfiguration() {
        this(null, null, null, null, null);
    }

    /**
     * Creates a project app configuration.
     *
     * @param generalConfig General configuration
     * @param bridgeConfig  Bridge configuration
     * @param reportConfig  Report configuration
     */
    public ProjectAppConfiguration(GeneralConfig generalConfig, BridgeConfig bridgeConfig, ReportConfig reportConfig, FieldsConfig fieldsConfig,
                                   JwtConfig jwtConfig) {
        this.generalConfig = Objects.requireNonNullElse(generalConfig, new GeneralConfig());
        this.bridgeConfig = Objects.requireNonNullElse(bridgeConfig, new BridgeConfig());
        this.reportConfig = Objects.requireNonNullElse(reportConfig, new ReportConfig());
        this.fieldsConfig = Objects.requireNonNullElse(fieldsConfig, new FieldsConfig());
        this.jwtConfig = Objects.requireNonNullElse(jwtConfig, new JwtConfig());
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

    public FieldsConfig getFieldsConfig() {
        return fieldsConfig;
    }

    public JwtConfig getJwtConfig() {
        return jwtConfig;
    }

    @Override
    public String toString() {
        return format("{generalConfig: [%s], bridgeConfig: [%s], reportConfig: [%s], fieldsConfig: [%s]}", generalConfig, bridgeConfig, reportConfig,
                      fieldsConfig, jwtConfig);
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
               Objects.equals(getReportConfig(), that.getReportConfig()) &&
               Objects.equals(getFieldsConfig(), that.getFieldsConfig()) &&
               Objects.equals(getJwtConfig(), that.getJwtConfig());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeneralConfig(), getBridgeConfig(), getReportConfig(), getFieldsConfig(), getJwtConfig());
    }
}
