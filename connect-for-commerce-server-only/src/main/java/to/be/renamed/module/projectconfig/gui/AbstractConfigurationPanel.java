package to.be.renamed.module.projectconfig.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import info.clearthought.layout.TableLayoutConstraints;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.BorderFactory;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Abstract configuration panel with helper functions for creating input fields.
 *
 * @param <ConfigurationT> The current configuration of the panel
 */
abstract class AbstractConfigurationPanel<ConfigurationT> {

    static final int TEXTFIELD_WIDTH = 80;
    static final int TEXTFIELD_COLUMNS = 30;
    private static final int TEXTFIELD_HEIGHT = 20;
    private static final int LAYOUT_H_GAP = 5;
    private static final int LAYOUT_V_GAP = 5;
    private static final int BORDER_SIZE = 5;
    private final TableLayout layout;
    private int row;
    private final JPanel panel;
    protected final ResourceBundle labels;

    /**
     * Sets the defined layout.
     * Adds the gui resource bundle.
     */
    protected AbstractConfigurationPanel() {
        labels = ResourceBundle.getBundle("projectApp");
        panel = new JPanel();
        layout = new TableLayout();
        layout.setColumn(new double[]{TableLayoutConstants.PREFERRED, TableLayoutConstants.FILL});
        layout.setHGap(LAYOUT_H_GAP);
        layout.setVGap(LAYOUT_V_GAP);
        panel.setLayout(layout);
        panel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        row = -1;
    }

    JPanel getPanel() {
        return panel;
    }

    /**
     * Adds the given component with the given label to the panel.
     *
     * @param component        The component to add
     * @param labelResourceKey The component label resource key
     */
    final void addComponent(final JComponent component, final Label labelResourceKey) {
        row++;
        layout.insertRow(row, TableLayoutConstants.PREFERRED);
        component.setPreferredSize(new Dimension(TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT));
        JLabel jLabel = new JLabel(labels.getString(labelResourceKey.getResourceBundleKey()));
        panel.add(jLabel, new TableLayoutConstraints(0, row));
        panel.add(component, new TableLayoutConstraints(1, row));
    }

    /**
     * Adds a button with the given label and button action.
     *
     * @param labelResourceKey The button label resource key
     * @param action           The action to execute when clicking the button
     */
    final void addButton(final Label labelResourceKey, Consumer<ActionEvent> action) {
        row++;
        layout.insertRow(row, TableLayoutConstants.PREFERRED);
        JButton button = new JButton(labels.getString(labelResourceKey.getResourceBundleKey()));
        button.setPreferredSize(new Dimension(TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT));
        button.addActionListener(action::accept);
        panel.add(button, new TableLayoutConstraints(1, row));
    }

    /**
     * Provides the configuration based on the panels input fields.
     *
     * @return The values from the panels input fields packed as the configuration object.
     */
    abstract ConfigurationT getValue();
}
