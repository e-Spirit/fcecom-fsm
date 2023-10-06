package to.be.renamed.module.projectconfig.connectiontest;

import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EcomTestConnectionResult extends JDialog {
    private static final long serialVersionUID = -3375713240936359305L;
    private final EcomTestConnectionLog logDisplayComponent;

    protected final ResourceBundle labels;
    private ExecutorService executorService;

    public ExecutorService createExecutorService() {
        executorService = Executors.newSingleThreadExecutor();
        return executorService;
    }

    public EcomTestConnectionResult(@NotNull final Dialog parent, @NotNull final String title) {
        super(parent, title, true);

        // Localization
        labels = ResourceBundle.getBundle("projectAppTestConnection");

        // Panel
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        final JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        logDisplayComponent = new EcomTestConnectionLog();
        panel.add(logDisplayComponent, BorderLayout.CENTER);

        // Buttons
        final JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        JButton copyToClipboardButton = new JButton(labels.getString("testConnection.buttons.copyToClipboard"));
        copyToClipboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyToClipboardButton.setPreferredSize(new Dimension(80, 20));
        copyToClipboardButton.addActionListener(buttonEvent -> logDisplayComponent.copyToClipboard());

        buttonPanel.add(copyToClipboardButton);

        JButton closeButton = new JButton(labels.getString("testConnection.buttons.close"));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setPreferredSize(new Dimension(80, 20));
        closeButton.addActionListener(buttonEvent -> dispose());

        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setSize(700, 700);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    public EcomTestConnectionLog getLogDisplayComponent() {
        return logDisplayComponent;
    }

    public String getFullLog() {
        return logDisplayComponent.getLog().getText();
    }
}