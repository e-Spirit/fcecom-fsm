package to.be.renamed.module.projectconfig.model;

import de.espirit.common.base.Logging;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

/**
 * Representation of the report configuration.
 */
public class ReportConfig implements Serializable {

    private static final long serialVersionUID = -177047415205831938L;
    private static final int DEFAULT_CATEGORY_LEVELS_FOR_CATEGORY_REPORT = 3;
    private static final int DEFAULT_CATEGORY_LEVELS_FOR_PRODUCT_REPORT = 3;
    private final int categoryReportCategoryLevel;
    private final int productReportCategoryLevel;

    /**
     * Required by Gson.
     * Creates a report configuration with default values.
     * (categoryReportCategoryLevel = 3 , productReportCategoryLevel = 3)
     */
    public ReportConfig() {
        this(DEFAULT_CATEGORY_LEVELS_FOR_CATEGORY_REPORT, DEFAULT_CATEGORY_LEVELS_FOR_PRODUCT_REPORT);
    }

    /**
     * Creates a report configuration.
     * @param categoryReportCategoryLevel Category levels for category report filter
     * @param productReportCategoryLevel Category levels for product report filter
     */
    public ReportConfig(int categoryReportCategoryLevel, int productReportCategoryLevel) {
        this.categoryReportCategoryLevel = Objects.requireNonNullElse(categoryReportCategoryLevel, DEFAULT_CATEGORY_LEVELS_FOR_CATEGORY_REPORT);
        this.productReportCategoryLevel = Objects.requireNonNullElse(productReportCategoryLevel, DEFAULT_CATEGORY_LEVELS_FOR_PRODUCT_REPORT);
    }

    /**
     * Creates a report configuration from strings, uses default values when input is not parseable to int.
     * (categoryReportCategoryLevel = 3, productReportCategoryLevel = 3)
     * Needed when reading values from file.
     * @param categoryReportCategoryLevel Category levels for category report filter as string
     * @param productReportCategoryLevel Category levels for product report filter
     * @return A report configuration
     */
    public static ReportConfig fromStrings(String categoryReportCategoryLevel, String productReportCategoryLevel) {
        int categoryReportCatLevel = DEFAULT_CATEGORY_LEVELS_FOR_CATEGORY_REPORT;
        int productReportCatLevel = DEFAULT_CATEGORY_LEVELS_FOR_PRODUCT_REPORT;

        try {
            categoryReportCatLevel = parseInt(categoryReportCategoryLevel);
        } catch (NumberFormatException nfe) {
            Logging.logWarning("Unable to parse configured category level for category search dropdown. Using default value '" + DEFAULT_CATEGORY_LEVELS_FOR_CATEGORY_REPORT + "'.",
                    nfe, ReportConfig.class);
        }

        try {
            productReportCatLevel = parseInt(productReportCategoryLevel);
        } catch (NumberFormatException nfe) {
            Logging.logWarning("Unable to parse configured category level for product search dropdown. Using default value '" + DEFAULT_CATEGORY_LEVELS_FOR_CATEGORY_REPORT + "'.",
                    nfe, ReportConfig.class);
        }

        return new ReportConfig(categoryReportCatLevel, productReportCatLevel);
    }

    public int getCategoryReportCategoryLevel() {
        return categoryReportCategoryLevel;
    }

    public int getProductReportCategoryLevel() {
        return productReportCategoryLevel;
    }

    public String getCategoryReportCategoryLevelAsString() {
        return String.valueOf(categoryReportCategoryLevel);
    }

    public String getProductReportCategoryLevelAsString() {
        return String.valueOf(productReportCategoryLevel);
    }

    @Override
    public String toString() {
        return format("{categoryReportCategoryLevel: %s, productReportCategoryLevel: %s}", categoryReportCategoryLevel, productReportCategoryLevel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReportConfig that = (ReportConfig) o;
        return getCategoryReportCategoryLevel() == that.getCategoryReportCategoryLevel() && getProductReportCategoryLevel() == that.getProductReportCategoryLevel();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategoryReportCategoryLevel(), getProductReportCategoryLevel());
    }
}
