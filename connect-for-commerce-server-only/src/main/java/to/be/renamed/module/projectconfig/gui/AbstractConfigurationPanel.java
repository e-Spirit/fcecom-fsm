package to.be.renamed.module.projectconfig.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.IntToDoubleFunction;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Abstract configuration panel with helper functions for creating input fields.
 *
 * @param <ConfigurationT> The current configuration of the panel
 */
abstract class AbstractConfigurationPanel<ConfigurationT> {

    private static final int TEXTFIELD_WIDTH = 80;
    protected static final int TEXTFIELD_COLUMNS = 35;
    private static final int TEXTFIELD_HEIGHT = 23;
    private static final int LAYOUT_H_GAP = 10;
    private static final int LAYOUT_V_GAP = 10;
    private static final int BORDER_SIZE = 10;
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
        addFocusDismiss(panel);
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
        addFocusDismiss(jLabel);

        panel.add(jLabel, new TableLayoutConstraints(0, row));
        panel.add(component, new TableLayoutConstraints(1, row));
    }

    final void addComponent(final JCheckBox component, final Label labelResourceKey) {
        // Panel to hold both buttons side by side
        JPanel checkBoxPanel = new JPanel();
        TableLayout tableLayout = new TableLayout(new double[][]{
            new double[]{TableLayoutConstants.PREFERRED, TableLayoutConstants.FILL},
            new double[]{TableLayoutConstants.PREFERRED}});

        checkBoxPanel.setLayout(tableLayout);
        checkBoxPanel.add(component, new TableLayoutConstraints(0, 0));

        addFocusDismiss(component);
        addComponent(checkBoxPanel, labelResourceKey);
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
        button.addActionListener(actionEvent -> {
            panel.requestFocusInWindow();
            action.accept(actionEvent);
        });
        panel.add(button, new TableLayoutConstraints(1, row));
    }

    /**
     * Adds a new row of buttons regarding the JWT Secret Input Field.
     *
     * @param generateJwtSecretButton A button to generate a random JWT Secret.
     * @param copyToClipboardButton   A button to copy the JWT Secret to the clipboard.
     * @param showHideSecretButton    A button to show or hide the JWT Token.
     */
    final void addJwtButtonRow(JButton generateJwtSecretButton, JButton copyToClipboardButton, JButton showHideSecretButton) {
        row++;
        layout.insertRow(row, TableLayoutConstants.PREFERRED);

        final int COLUMNS = 3;
        final int ROWS = 1;

        final IntToDoubleFunction contentStretch = i -> TableLayoutConstants.PREFERRED;

        double[] columns = new double[COLUMNS];
        Arrays.setAll(columns, contentStretch);

        double[] rows = new double[ROWS];
        Arrays.setAll(rows, contentStretch);

        // Panel to hold both buttons side by side
        JPanel buttonPanel = new JPanel();
        TableLayout tableLayout = new TableLayout(new double[][]{columns, rows});

        tableLayout.setHGap(LAYOUT_H_GAP);
        tableLayout.setVGap(LAYOUT_V_GAP);

        buttonPanel.setLayout(tableLayout);

        final int GENERATE_JWT_SECRET_BUTTON_POSITION = 0;
        final int COPY_TO_CLIPBOARD_BUTTON_POSITION = 1;
        final int SHOW_SECRET_BUTTON_POSITION = 2;

        buttonPanel.add(generateJwtSecretButton, new TableLayoutConstraints(GENERATE_JWT_SECRET_BUTTON_POSITION, 0));
        buttonPanel.add(copyToClipboardButton, new TableLayoutConstraints(COPY_TO_CLIPBOARD_BUTTON_POSITION, 0));
        buttonPanel.add(showHideSecretButton, new TableLayoutConstraints(SHOW_SECRET_BUTTON_POSITION, 0));

        // Add the button panel to the main panel
        panel.add(buttonPanel, new TableLayoutConstraints(1, row));
    }

    /**
     * Updates the text of a button and adjusts its size accordingly.
     *
     * @param button  The button to update
     * @param newText The new text to set on the button
     */
    void updateButtonText(JButton button, String newText) {
        button.setText(newText);

        // Reset to default sizing based on content
        button.setPreferredSize(null);

        // Recalculate the layout for the button
        button.revalidate();

        // Redraw the button
        button.repaint();
    }

    void dismissFocus() {
        panel.requestFocusInWindow();
    }

    // Add mouse listener to remove focus when clicking outside components
    private void addFocusDismiss(final JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                dismissFocus();
            }
        });
    }

    /**
     * Provides the configuration based on the panels input fields.
     *
     * @return The values from the panels input fields packed as the configuration object.
     */
    abstract ConfigurationT getValue();
}
