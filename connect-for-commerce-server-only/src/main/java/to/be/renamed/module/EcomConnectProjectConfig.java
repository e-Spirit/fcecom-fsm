package to.be.renamed.module;

import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.module.projectconfig.gui.ConfigurationAppPanel;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.UIAgent;
import de.espirit.firstspirit.module.Configuration;
import de.espirit.firstspirit.module.ProjectEnvironment;

import java.awt.*;
import java.util.Collections;
import java.util.Set;

import javax.swing.*;

public class EcomConnectProjectConfig implements Configuration<ProjectEnvironment> {

    private ProjectEnvironment projectEnvironment;
    private ProjectAppConfiguration configuration;
    private ConfigurationAppPanel configurationPanel;
    private Language language;
    private ProjectAppConfigurationService moduleConfigurationAccessor;

    @Override
    public void init(final String moduleName, final String componentName, final ProjectEnvironment env) {
        this.projectEnvironment = env;
        moduleConfigurationAccessor = ServiceFactory.getProjectAppConfigurationService(projectEnvironment.getBroker());

        final UIAgent uiAgent = env.getBroker().requestSpecialist(UIAgent.TYPE);
        language = uiAgent != null ? uiAgent.getDisplayLanguage() : null;

        if (language == null) {
            language = env.getBroker().requireSpecialist(LanguageAgent.TYPE).getMasterLanguage();
        }
    }

    @Override
    public ProjectEnvironment getEnvironment() {
        return projectEnvironment;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public JComponent getGui(Frame appFrame) {
        if (configurationPanel == null) {
            configurationPanel = new ConfigurationAppPanel(configuration, projectEnvironment.getBroker());
        }
        if (appFrame != null) {
            return (JComponent) appFrame.add(configurationPanel.getConfigurationPanel());
        } else {
            return configurationPanel.getConfigurationPanel();
        }
    }

    @Override
    public void load() {
        Logging.logDebug("Loading project app configuration", getClass());
        configuration = moduleConfigurationAccessor.loadConfiguration();
    }

    @Override
    public void store() {
        Logging.logDebug("Storing project app configuration", getClass());
        final ProjectAppConfiguration updatedConfiguration = configurationPanel.getValue();
        moduleConfigurationAccessor.storeConfiguration(updatedConfiguration);
        configuration = updatedConfiguration;
        ServiceFactory.getBridgeService(projectEnvironment.getBroker())
                .configureBridge(updatedConfiguration.getBridgeConfig());
    }

    // Unimplemented because not needed
    @Override
    public String getParameter(final String name) { return ""; }

    // Unimplemented because not needed
    @Override
    public Set<String> getParameterNames() { return Collections.emptySet(); }

}
