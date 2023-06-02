package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.module.projectconfig.model.BridgeConfig;
import to.be.renamed.module.projectconfig.model.GeneralConfig;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import to.be.renamed.module.projectconfig.model.ReportConfig;

import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Configuration panel for the project configuration.
 */
public class ConfigurationAppPanel extends AbstractConfigurationPanel<ProjectAppConfiguration> {

    private final GeneralConfigurationPanel generalTab;
    private final BridgeConfigurationPanel bridgeTab;
    private final ReportConfigurationPanel reportTab;
    private final JPanel configurationPanel;

    /**
     * Creates a configuration panel for the project app configuration.
     * Combines general, bridge and report tabs.
     * @param projectAppConfiguration The current configuration values
     */
    public ConfigurationAppPanel(final ProjectAppConfiguration projectAppConfiguration, SpecialistsBroker broker) {
        super();
        generalTab = new GeneralConfigurationPanel(projectAppConfiguration.getGeneralConfig());
        bridgeTab = new BridgeConfigurationPanel(projectAppConfiguration.getBridgeConfig(), broker);
        reportTab = new ReportConfigurationPanel(projectAppConfiguration.getReportConfig());

        configurationPanel = new JPanel();
        configurationPanel.setLayout(new BoxLayout(configurationPanel, BoxLayout.PAGE_AXIS));
        JTabbedPane jTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane.addTab(labels.getString(Label.GENERAL_TAB_TITLE.getResourceBundleKey()), generalTab.getPanel());
        jTabbedPane.addTab(labels.getString(Label.BRIDGE_TAB_TITLE.getResourceBundleKey()), bridgeTab.getPanel());
        jTabbedPane.addTab(labels.getString(Label.REPORT_TAB_TITLE.getResourceBundleKey()), reportTab.getPanel());
        configurationPanel.add(jTabbedPane);
    }

    public JComponent getConfigurationPanel() {
        return configurationPanel;
    }

    /**
     * Provides the ProjectAppConfiguration based on the panels input fields of each tab.
     * @return The values from the panels input fields of each tab packed as a ProjectAppConfiguration object.
     */
    @Override
    public ProjectAppConfiguration getValue() {
        final GeneralConfig generalConfigValue = generalTab.getValue();
        final BridgeConfig bridgeConfigValue = bridgeTab.getValue();
        final ReportConfig reportConfigValue = reportTab.getValue();

        return new ProjectAppConfiguration(generalConfigValue, bridgeConfigValue,reportConfigValue);
    }
}
