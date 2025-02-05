package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.module.projectconfig.model.JwtConfig;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.util.Base64URL;

import de.espirit.common.base.Logging;

import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import static java.awt.Toolkit.getDefaultToolkit;

/**
 * Configuration panel for jwt related configuration tab.
 */
public class JwtConfigurationPanel extends AbstractConfigurationPanel<JwtConfig> {

    private final JPasswordField jwtSecret;
    private final JButton showHideSecretButton;

    /**
     * Creates a configuration panel for the jwt related configuration tab.
     *
     * @param jwtConfig The current jwt configuration values
     */
    public JwtConfigurationPanel(JwtConfig jwtConfig) {
        super();

        jwtSecret = new JPasswordField(jwtConfig.getJwtSecret().toString(), TEXTFIELD_COLUMNS);
        jwtSecret.setEchoChar('•');
        jwtSecret.putClientProperty("JPasswordField.cutCopyAllowed", true);
        addComponent(jwtSecret, Label.JWT_SECRET);

        JButton generateJwtSecretButton = new JButton(labels.getString(Label.GENERATE_JWT_SECRET.getResourceBundleKey()));
        generateJwtSecretButton.addActionListener(this::generateJwtSecret);

        JButton copyToClipboardButton = new JButton(labels.getString(Label.COPY_JWT_SECRET.getResourceBundleKey()));
        copyToClipboardButton.addActionListener(this::copyToClipboard);

        showHideSecretButton = new JButton();
        showHideSecretButton.addActionListener(this::showAndHideSecret);

        addJwtButtonRow(generateJwtSecretButton, copyToClipboardButton, showHideSecretButton);
        updateButtonText(showHideSecretButton, labels.getString(Label.SHOW_PASSWORD.getResourceBundleKey()));
    }

    private void copyToClipboard(ActionEvent actionEvent) {
        dismissFocus();
        getDefaultToolkit().getSystemClipboard()
            .setContents(new StringSelection(String.valueOf(jwtSecret.getPassword())), null);
    }

    private void generateJwtSecret(ActionEvent actionEvent) {
        try {
            try {
                // Generate a secret key with 256 bits
                SecretKey hmacKey = KeyGenerator.getInstance("HmacSha256").generateKey();

                // Convert to JWK format
                OctetSequenceKey jwk = new OctetSequenceKey.Builder(hmacKey)
                    .algorithm(JWSAlgorithm.HS256).build()
                    .toOctetSequenceKey();

                jwtSecret.setText(jwk.getKeyValue().toString());
            } catch (NoSuchAlgorithmException exception) {
                // Degrade to less secure, but more widely supported algorithms.
                Logging.logError(
                    "Degrade to less secure, but more widely supported algorithms because this system doesn't support it.",
                    exception, getClass());
                OctetSequenceKey jwk = new OctetSequenceKeyGenerator(256).algorithm(JWSAlgorithm.HS256).generate();

                jwtSecret.setText(jwk.getKeyValue().toString());
            }

            showSecret();
        } catch (JOSEException exception) {
            Logging.logError("Failed to generate JWT Secret", exception, getClass());
            JOptionPane.showMessageDialog(null, exception, "Failed to generate JWT Secret", JOptionPane.ERROR_MESSAGE);
        } finally {
            dismissFocus();
        }
    }

    private void showSecret() {
        jwtSecret.setEchoChar((char) 0);
        updateButtonText(showHideSecretButton, labels.getString(Label.HIDE_PASSWORD.getResourceBundleKey()));
    }

    private void hideSecret() {
        jwtSecret.setEchoChar('•');
        updateButtonText(showHideSecretButton, labels.getString(Label.SHOW_PASSWORD.getResourceBundleKey()));
    }

    private void showAndHideSecret(ActionEvent actionEvent) {
        if (labels.getString(Label.SHOW_PASSWORD.getResourceBundleKey()).equals(showHideSecretButton.getText())) {
            showSecret();
        } else {
            hideSecret();
        }

        dismissFocus();
    }

    /**
     * Provides the JwtConfig based on the panels input fields.
     *
     * @return The values from the panels input fields packed as a JwtConfig object.
     */
    @Override
    JwtConfig getValue() {
        return new JwtConfig(new Base64URL(String.valueOf(jwtSecret.getPassword())));
    }
}
