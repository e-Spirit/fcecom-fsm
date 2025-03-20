package to.be.renamed.module.projectconfig.gui;

import de.espirit.common.base.Logging;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;
import info.clearthought.layout.TableLayoutConstraints;

import org.intellij.lang.annotations.Language;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.IntToDoubleFunction;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;

/**
 * Abstract configuration panel with helper functions for creating input fields.
 *
 * @param <ConfigurationT> The current configuration of the panel
 */
abstract class AbstractConfigurationPanel<ConfigurationT> {

    protected static final int TEXTFIELD_WIDTH = 80;
    protected static final int TEXTFIELD_COLUMNS = 35;
    protected static final int TEXTFIELD_HEIGHT = 23;
    private static final int LAYOUT_H_GAP = 10;
    private static final int LAYOUT_V_GAP = 10;
    private static final int BORDER_SIZE = 10;
    protected final TableLayout layout;
    private int row;
    protected final JPanel panel;
    protected final JPanel containerPanel;
    private final JPanel verticalPanel;
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
        panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        addFocusDismiss(panel);
        row = -1;

        // A sub-panel to stack the table panel and the doc text vertically
        verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.add(panel);

        // Container Panel with BorderLayout
        containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        containerPanel.add(verticalPanel, BorderLayout.NORTH);
    }

    JPanel getPanel() {
        return containerPanel;
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

    /**
     * Adds the given component with the given label to the panel.
     *
     * @param component The component to add
     * @return row index
     */
    final int addComponent(final JComponent component) {
        row++;
        layout.insertRow(row, TableLayoutConstants.PREFERRED);
        component.setPreferredSize(new Dimension(TEXTFIELD_WIDTH, TEXTFIELD_HEIGHT));
        panel.add(component, new TableLayoutConstraints(1, row));

        return row;
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

    final void addRichText(final Label labelResourceKey) {
        JEditorPane docPane = new JEditorPane();
        docPane.setContentType("text/html");
        docPane.setText(styledHtml(labelResourceKey));

        docPane.setEditable(false);
        docPane.setOpaque(false);
        docPane.setBackground(Color.WHITE);

        // Open link in browser when the user clicks on it.
        docPane.addHyperlinkListener(this::hyperLinkClick);

        docPane.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        docPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, docPane.getPreferredSize().height));
        docPane.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, 0, BORDER_SIZE, 0));

        addFocusDismiss(docPane);

        verticalPanel.add(docPane);
        verticalPanel.revalidate();
        verticalPanel.repaint();
    }

    private String styledHtml(final Label labelResourceKey) {
        @Language("HTML") String styledHtml = """
            <html>
            <head>
                <style>
                    body, a {
                        color: #666666;
                        margin: 0;
                        padding: 0;
                    }
                </style>
            </head>
            <body>
                %s
            </body>
            </html>
            """.formatted(labels.getString(labelResourceKey.getResourceBundleKey()));

        return styledHtml;
    }

    private void hyperLinkClick(HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                // Opens the standard browser of the users' OS
                Desktop.getDesktop().browse(event.getURL().toURI());
            } catch (HeadlessException exception) {
                Logging.logError("Cannot open link in headless system", exception, getClass());
            } catch (IOException exception) {
                Logging.logError("Could not open link in user's browser.", exception, getClass());
            } catch (URISyntaxException exception) {
                Logging.logError("URL to open is not valid.", exception, getClass());
            }
        }
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
