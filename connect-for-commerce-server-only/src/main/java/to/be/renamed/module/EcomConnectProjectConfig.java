package to.be.renamed.module;

import to.be.renamed.bridge.BridgeService;
import to.be.renamed.caas.CaasService;
import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.module.projectconfig.gui.ConfigurationAppPanel;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.project.Project;
import de.espirit.firstspirit.module.Configuration;
import de.espirit.firstspirit.module.ProjectEnvironment;

import java.awt.Frame;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.swing.JComponent;

/**
 * Provides all information necessary to run all customer-specific operations.
 */
public class EcomConnectProjectConfig implements Configuration<ProjectEnvironment> {

    private ProjectEnvironment projectEnvironment;
    private ProjectAppConfiguration configuration;
    private ConfigurationAppPanel configurationPanel;
    private ProjectAppConfigurationService moduleConfigurationAccessor;

    @Override
    public void init(final String moduleName, final String componentName, final ProjectEnvironment env) {
        this.projectEnvironment = env;
        moduleConfigurationAccessor = ServiceFactory.getProjectAppConfigurationService(projectEnvironment.getBroker());
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
            configurationPanel = new ConfigurationAppPanel(configuration, projectEnvironment);
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
        configuration.getBridgeConfig().setProjectUuid(Objects.requireNonNull(projectEnvironment.getProject()).getUuid());
    }

    @Override
    public void store() {
        Logging.logDebug("Storing project app configuration", getClass());
        final ProjectAppConfiguration updatedConfiguration = configurationPanel.getValue();
        moduleConfigurationAccessor.storeConfiguration(updatedConfiguration);
        configuration = updatedConfiguration;
        final BridgeService bridgeService = ServiceFactory.getBridgeService(projectEnvironment.getBroker());
        final Project project = Objects.requireNonNull(projectEnvironment.getProject());
        updatedConfiguration.getBridgeConfig().setProjectUuid(project.getUuid());
        bridgeService.configureBridge(updatedConfiguration.getBridgeConfig());
        final CaasService caasService = ServiceFactory.getCaasService(projectEnvironment);
        caasService.addCaasIndex(configuration.getFieldsConfig());
    }

    // Unimplemented because not needed
    @Override
    public String getParameter(final String name) {
        return "";
    }

    // Unimplemented because not needed
    @Override
    public Set<String> getParameterNames() {
        return Collections.emptySet();
    }

}
