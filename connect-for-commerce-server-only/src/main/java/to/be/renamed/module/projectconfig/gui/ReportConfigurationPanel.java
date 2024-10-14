package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.module.projectconfig.model.ReportConfig;

import javax.swing.JTextField;

/**
 * Configuration panel for report related configuration tab.
 */
public class ReportConfigurationPanel extends AbstractConfigurationPanel<ReportConfig> {

    private final JTextField categoryReportCategoryLevelTextField;
    private final JTextField productReportCategoryLevelTextField;

    /**
     * Creates a configuration panel for the report related configuration tab.
     *
     * @param reportConfig The current report configuration values
     */
    public ReportConfigurationPanel(ReportConfig reportConfig) {
        super();
        categoryReportCategoryLevelTextField = new JTextField(reportConfig.getCategoryReportCategoryLevelAsString(), TEXTFIELD_COLUMNS);
        addComponent(categoryReportCategoryLevelTextField, Label.REPORT_CATEGORY_LEVELS);

        productReportCategoryLevelTextField = new JTextField(reportConfig.getProductReportCategoryLevelAsString(), TEXTFIELD_COLUMNS);
        addComponent(productReportCategoryLevelTextField, Label.REPORT_PRODUCT_LEVELS);
    }

    /**
     * Provides the ReportConfig based on the panels input fields.
     *
     * @return The values from the panels input fields packed as a ReportConfig object.
     */
    @Override
    ReportConfig getValue() {
        return ReportConfig.fromStrings(categoryReportCategoryLevelTextField.getText(), productReportCategoryLevelTextField.getText());
    }
}
