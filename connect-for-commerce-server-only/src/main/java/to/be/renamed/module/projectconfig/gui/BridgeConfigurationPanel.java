package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.TaskType;
import to.be.renamed.bridge.TestConnectionRequest;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import to.be.renamed.module.projectconfig.connectiontest.EcomTestConnectionLog;
import to.be.renamed.module.projectconfig.connectiontest.EcomTestConnectionResult;
import to.be.renamed.module.projectconfig.model.BridgeConfig;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import kong.unirest.HttpMethod;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Configuration panel for bridge related configuration tab.
 */
public class BridgeConfigurationPanel extends AbstractConfigurationPanel<BridgeConfig> {

    private final JTextField bridgeApiUrlTextField;
    private final JTextField bridgeApiUsernameTextField;
    private final JPasswordField bridgeApiPasswordPasswordField;
    private final CacheConfigurationPanel cacheConfigurationPanel;

    /**
     * Creates a configuration panel for the bridge related configuration tab.
     *
     * @param bridgeConfig The current bridge configuration values
     */
    BridgeConfigurationPanel(final BridgeConfig bridgeConfig, SpecialistsBroker broker, CacheConfigurationPanel cacheConfigurationPanel) {
        super();

        bridgeApiUrlTextField = new JTextField(bridgeConfig.getBridgeApiUrl(), TEXTFIELD_COLUMNS);
        addComponent(bridgeApiUrlTextField, Label.BRIDGE_API_URL);

        bridgeApiUsernameTextField = new JTextField(bridgeConfig.getBridgeUsername(), TEXTFIELD_COLUMNS);
        addComponent(bridgeApiUsernameTextField, Label.BRIDGE_USERNAME);

        bridgeApiPasswordPasswordField = new JPasswordField(bridgeConfig.getBridgePassword(), TEXTFIELD_COLUMNS);
        addComponent(bridgeApiPasswordPasswordField, Label.BRIDGE_PASSWORD);

        this.cacheConfigurationPanel = cacheConfigurationPanel;

        Consumer<ActionEvent> buttonAction = e -> {
            final JDialog jOptionPane = new JOptionPane().createDialog("hello");

            String title = ResourceBundle.getBundle("projectApp").getString(Label.TEST_CONNECTION.getResourceBundleKey());

            final EcomTestConnectionResult progressDialog = new EcomTestConnectionResult(jOptionPane, title);
            final EcomTestConnectionLog log = progressDialog.getLogDisplayComponent();

            // Show dialog
            positionCenter(progressDialog);

            final ExecutorService executorService = progressDialog.createExecutorService();

            try {
                executorService.execute(() -> SwingUtilities.invokeLater(() -> progressDialog.setVisible(true)));

                executorService.execute(() -> {
                    try {
                        // Category
                        SwingUtilities.invokeAndWait(() -> {
                            log.heading(TaskType.CATEGORY);
                            log.newLine().addTaskDescription("testConnection.task.category");
                        });
                        SwingUtilities.invokeAndWait(() -> {
                            log.logResult(funcTest(broker, "/categories/ids"));
                        });

                        // Category Tree
                        SwingUtilities.invokeAndWait(() -> {
                            log.newLine().addTaskDescription("testConnection.task.categoryTree");
                        });
                        SwingUtilities.invokeAndWait(() -> {
                            log.logResult(funcTest(broker, "/categories/tree"));
                            log.newLine().newLine();
                        });

                        // Product
                        SwingUtilities.invokeAndWait(() -> {
                            log.heading(TaskType.PRODUCT);
                            log.newLine().addTaskDescription("testConnection.task.product");
                        });
                        SwingUtilities.invokeAndWait(() -> {
                            log.logResult(funcTest(broker, "/products/ids"));
                            log.newLine().newLine();
                        });

                        // Content (New)
                        SwingUtilities.invokeAndWait(() -> {
                            log.heading(TaskType.CONTENT);
                            log.newLine().addTaskDescription("testConnection.task.content");
                        });
                        SwingUtilities.invokeAndWait(() -> log.logResult(funcTest(broker, "/content")));

                        // Log to console
                        SwingUtilities.invokeLater(() -> Logging.logInfo("\nFCECOM TestConnection\n\n" + progressDialog.getFullLog(), getClass()));
                    } catch (InterruptedException exception) {
                        Logging.logInfo("Thread was interrupted and will be closed now: " + exception.getLocalizedMessage(), getClass());
                        if (Thread.interrupted()) {
                            Thread.currentThread().interrupt();
                        }
                    } catch (InvocationTargetException exception) {
                        Logging.logWarning("Having problems with testing the connection:", exception, getClass());
                    }
                });
            } catch (Exception exception) {
                Logging.logError("Failed to run TestConnection", exception, getClass());
                JOptionPane.showMessageDialog(null, exception, "Failed to run TestConnection", JOptionPane.ERROR_MESSAGE);
            } finally {
                executorService.shutdown();
            }
        };

        addButton(Label.BRIDGE_TEST_CONNECTION, buttonAction);
    }

    private BridgeTestResult funcTest(SpecialistsBroker broker, String url) {
        BridgeConfig updatedBridgeConfig = getValue();
        BridgeService bridgeService = ServiceFactory.getBridgeService(broker);
        return bridgeService.testConnection(updatedBridgeConfig, TestConnectionRequest.withParams(HttpMethod.HEAD.name(), url));
    }

    private static void positionCenter(EcomTestConnectionResult progressDialog) {
        Window owner = progressDialog.getOwner();
        Point p = owner.getLocation();
        progressDialog.setLocation(p.x + owner.getWidth() / 2 - progressDialog.getWidth()
                / 2, p.y + owner.getHeight() / 2 - progressDialog.getHeight() / 2);
    }

    /**
     * Provides the BridgeConfig based on the panels input fields.
     *
     * @return The values from the panels input fields packed as a BridgeConfig object.
     */
    @Override
    BridgeConfig getValue() {
        return BridgeConfig.fromValues(bridgeApiUrlTextField.getText(), bridgeApiUsernameTextField.getText(), bridgeApiPasswordPasswordField.getPassword(), cacheConfigurationPanel.getValue());
    }
}
