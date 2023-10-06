package to.be.renamed.module.projectconfig.connectiontest;

import to.be.renamed.bridge.TaskType;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Objects;
import java.util.ResourceBundle;

import static to.be.renamed.module.projectconfig.connectiontest.EcomTaskResult.UNKNOWN;
import static de.espirit.common.base.Logging.logWarning;
import static de.espirit.common.tools.Strings.isEmpty;
import static java.awt.Color.WHITE;

public class EcomTestConnectionLog extends JScrollPane {
    private static final long serialVersionUID = 4337670428843842562L;
    private final JTextPane log;

    private final ResourceBundle labels;

    public EcomTestConnectionLog() {
        labels = ResourceBundle.getBundle("projectAppTestConnection");

        getVerticalScrollBar().setUnitIncrement(10);
        final JPanel jp = new JPanel(new GridLayout(1, 1));
        setViewportView(jp);

        // create editor for output
        log = new JTextPane(new DefaultStyledDocument());
        log.setEditable(false);
        log.setBackground(WHITE);
        jp.add(log);
    }

    private void insertText(final SimpleAttributeSet attr, final String text) {
        try {
            log.setCharacterAttributes(attr, false);
            log.getDocument().insertString(log.getDocument().getLength(), text, attr);
            log.setCaretPosition(log.getDocument().getLength());
        } catch (BadLocationException exception) {
            logWarning("Problem inserting text for testing connection:", exception, getClass());
        }
    }

    public EcomTestConnectionLog newLine() {
        insertText(new SimpleAttributeSet(), "\n");
        return this;
    }

    private EcomTestConnectionLog inColor(final Color color, final String text) {
        final SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, color);
        insertText(attr, text);
        return this;
    }

    private void standard(final String text) {
        inColor(new Color(23, 43, 77), text);
    }

    private void light(final String text) {
        inColor(new Color(193, 199, 208), text);
    }

    private EcomTestConnectionLog warning(final String text) {
        inColor(new Color(255, 116, 82), text);
        return this;
    }

    private EcomTestConnectionLog info(final String text) {
        inColor(new Color(255, 171, 0), text);
        return this;
    }

    public void heading(final TaskType taskType) {
        final SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setForeground(attr, new Color(193, 199, 208));
        StyleConstants.setBold(attr, true);
        StyleConstants.setFontSize(attr, 18);

        insertText(attr, labels.getString("testConnection.heading." + taskType.name()));
        newLine();
    }

    public void addTaskDescription(final String task) {
        standard(labels.getString(task) + ": ");
    }

    public void addTaskResult(final BridgeTestResult testResult) {
        final EcomTaskResult taskResult = Objects.requireNonNullElse(testResult.getTaskResult(), UNKNOWN);

        inColor(taskResult.getColor(), labels.getString("testConnection.status." + taskResult.name())
                + " · " + labels.getString("testConnection.summary." + taskResult.name())).newLine();

        if (testResult.getExceptionMessage() != null) {
            warning(testResult.getExceptionMessage()).newLine();
        }

    }

    public EcomTestConnectionLog deprecated(final boolean deprecated) {
        if (deprecated) {
            info(labels.getString("testConnection.deprecationNotice")).newLine();
        }
        return this;
    }

    public void addDetails(final String details) {
        if (!isEmpty(details)) {
            light(labels.getString("testConnection.request") + " " + details);
            newLine();
        }
    }

    public void addParsingError(final String parsingError) {
        if (!isEmpty(parsingError)) {
            warning(labels.getString("testConnection.parsingError") + " " + parsingError);
            newLine();
        }
    }

    public void addErrorResponse(final String errorResponse) {
        if (!isEmpty(errorResponse)) {
            warning(labels.getString("testConnection.errorResponse") + " " + errorResponse);
            newLine();
        }
    }

    private void deselect() {
        int end = log.getSelectionEnd();
        log.setSelectionStart(end);
        log.setSelectionEnd(end);
    }

    public void copyToClipboard() {
        log.selectAll();
        log.setFocusable(true);
        log.copy();
        deselect();
    }

    public void logResult(final BridgeTestResult result) {
        addTaskResult(result);

        addParsingError(result.getParsingError());
        addErrorResponse(result.getErrorResponse());

        deprecated(result.isDeprecated());
        addDetails(result.summarizeRequest());
    }

    public JTextPane getLog() {
        return this.log;
    }
}