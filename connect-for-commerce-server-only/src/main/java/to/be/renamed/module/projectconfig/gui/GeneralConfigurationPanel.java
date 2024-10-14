package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.module.projectconfig.model.GeneralConfig;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 * Configuration panel for general related configuration tab.
 */
public class GeneralConfigurationPanel extends AbstractConfigurationPanel<GeneralConfig> {

    private final JTextField ccExtensionsUrlTextField;
    private final JCheckBox useCCExtensionsCheckBox;
    private final JCheckBox disableBridgePageCreation;

    /**
     * Creates a configuration panel for the general related configuration tab.
     *
     * @param generalConfig The current general configuration values
     */
    public GeneralConfigurationPanel(GeneralConfig generalConfig) {
        super();

        ccExtensionsUrlTextField = new JTextField(generalConfig.getCcExtensionsUrl(), TEXTFIELD_COLUMNS);
        addComponent(ccExtensionsUrlTextField, Label.CONTENT_CREATOR_EXTENSION);

        useCCExtensionsCheckBox = new JCheckBox("", generalConfig.useCCExtensions());
        addComponent(useCCExtensionsCheckBox, Label.USE_CONTENT_CREATOR_EXTENSION);

        disableBridgePageCreation = new JCheckBox("", generalConfig.disableBridgePageCreation());
        addComponent(disableBridgePageCreation, Label.DISABLE_BRIDGE_PAGE_CREATION);
    }

    /**
     * Provides the GeneralConfig based on the panels input fields.
     *
     * @return The values from the panels input fields packed as a GeneralConfig object.
     */
    @Override
    GeneralConfig getValue() {
        return new GeneralConfig(ccExtensionsUrlTextField.getText(), useCCExtensionsCheckBox.isSelected(), disableBridgePageCreation.isSelected());
    }
}
