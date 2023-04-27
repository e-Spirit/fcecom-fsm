package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.bridge.BridgeService;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.model.BridgeConfig;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * Configuration panel for bridge related configuration tab.
 */
public class BridgeConfigurationPanel extends AbstractConfigurationPanel<BridgeConfig> {

    private final JTextField bridgeApiUrlTextField;
    private final JTextField bridgeApiUsernameTextField;
    private final JPasswordField bridgeApiPasswordPasswordField;

    /**
     * Creates a configuration panel for the bridge related configuration tab.
     * @param bridgeConfig The current bridge configuration values
     */
    BridgeConfigurationPanel(final BridgeConfig bridgeConfig, SpecialistsBroker broker) {
        super();

        bridgeApiUrlTextField = new JTextField(bridgeConfig.getBridgeApiUrl(), TEXTFIELD_COLUMNS);
        addComponent(bridgeApiUrlTextField, Label.BRIDGE_API_URL);

        bridgeApiUsernameTextField = new JTextField(bridgeConfig.getBridgeUsername(), TEXTFIELD_COLUMNS);
        addComponent(bridgeApiUsernameTextField, Label.BRIDGE_USERNAME);

        bridgeApiPasswordPasswordField = new JPasswordField(bridgeConfig.getBridgePassword(), TEXTFIELD_COLUMNS);
        addComponent(bridgeApiPasswordPasswordField, Label.BRIDGE_PASSWORD);

        Consumer<ActionEvent> buttonAction = e -> {
            BridgeConfig updatedBridgeConfig = getValue();
            BridgeService bridgeService = ServiceFactory.getBridgeService(broker);
            JOptionPane.showMessageDialog(null, bridgeService.testConnection(updatedBridgeConfig).toString());

        };

        addButton(Label.BRIDGE_TEST_CONNECTION, buttonAction);
    }

    /**
     * Provides the BridgeConfig based on the panels input fields.
     * @return The values from the panels input fields packed as a BridgeConfig object.
     */
    @Override
    BridgeConfig getValue() {
        return BridgeConfig.fromValues(bridgeApiUrlTextField.getText(), bridgeApiUsernameTextField.getText(), bridgeApiPasswordPasswordField.getPassword());
    }
}
