package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.module.projectconfig.model.BridgeConfig;
import to.be.renamed.module.projectconfig.model.GeneralConfig;
import to.be.renamed.module.projectconfig.model.JwtConfig;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import to.be.renamed.module.projectconfig.model.ReportConfig;

import de.espirit.firstspirit.module.ProjectEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Configuration panel for the project configuration.
 */
public class ConfigurationAppPanel extends AbstractConfigurationPanel<ProjectAppConfiguration> {

    private final GeneralConfigurationPanel generalTab;
    private final BridgeConfigurationPanel bridgeTab;
    private final ReportConfigurationPanel reportTab;
    private final CacheConfigurationPanel cacheTab;
    private final JwtConfigurationPanel jwtTab;
    private final JPanel configurationPanel;

    /**
     * Creates a configuration panel for the project app configuration.
     * Combines general, bridge and report tabs.
     *
     * @param projectAppConfiguration The current configuration values
     */
    public ConfigurationAppPanel(final ProjectAppConfiguration projectAppConfiguration, ProjectEnvironment projectEnvironment) {
        super();
        generalTab = new GeneralConfigurationPanel(projectAppConfiguration.getGeneralConfig());
        cacheTab = new CacheConfigurationPanel(projectAppConfiguration.getBridgeConfig().getCacheConfig());
        bridgeTab = new BridgeConfigurationPanel(projectAppConfiguration.getBridgeConfig(), projectEnvironment, cacheTab);
        reportTab = new ReportConfigurationPanel(projectAppConfiguration.getReportConfig());
        jwtTab = new JwtConfigurationPanel(projectAppConfiguration.getJwtConfig());

        configurationPanel = new JPanel();
        configurationPanel.setLayout(new BoxLayout(configurationPanel, BoxLayout.PAGE_AXIS));
        JTabbedPane jTabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane.addTab(labels.getString(Label.GENERAL_TAB_TITLE.getResourceBundleKey()), generalTab.getPanel());
        jTabbedPane.addTab(labels.getString(Label.BRIDGE_TAB_TITLE.getResourceBundleKey()), bridgeTab.getPanel());
        jTabbedPane.addTab(labels.getString(Label.REPORT_TAB_TITLE.getResourceBundleKey()), reportTab.getPanel());
        jTabbedPane.addTab(labels.getString(Label.CACHE_TAB_TITLE.getResourceBundleKey()), cacheTab.getPanel());
        jTabbedPane.addTab(labels.getString(Label.JWT_TAB_TITLE.getResourceBundleKey()), jwtTab.getPanel());

        // Remove focus when a tab is opened
        jTabbedPane.addChangeListener(changeEvent -> {
            SwingUtilities.invokeLater(() -> jTabbedPane.getSelectedComponent().requestFocusInWindow());
        });

        configurationPanel.add(jTabbedPane);
    }

    public JComponent getConfigurationPanel() {
        return configurationPanel;
    }

    /**
     * Provides the ProjectAppConfiguration based on the panels input fields of each tab.
     *
     * @return The values from the panels input fields of each tab packed as a ProjectAppConfiguration object.
     */
    @Override
    public ProjectAppConfiguration getValue() {
        final GeneralConfig generalConfigValue = generalTab.getValue();
        final BridgeConfig bridgeConfigValue = bridgeTab.getValue();
        final ReportConfig reportConfigValue = reportTab.getValue();
        final JwtConfig jwtConfigValue = jwtTab.getValue();

        return new ProjectAppConfiguration(generalConfigValue, bridgeConfigValue, reportConfigValue, jwtConfigValue);
    }
}
