package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.module.projectconfig.model.CacheConfig;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import java.text.ParseException;

/**
 * Configuration panel for cache related configuration tab.
 */
public class CacheConfigurationPanel extends AbstractConfigurationPanel<CacheConfig> {

    private final JTextField cacheSizeTextField;
    private final JTextField cacheAgeTextField;

    /**
     * Creates a configuration panel for the cache related configuration tab.
     *
     * @param cacheConfig The current cache configuration values
     */
    public CacheConfigurationPanel(CacheConfig cacheConfig) {
        super();
        cacheSizeTextField = new JTextField(cacheConfig.getCacheSizeAsString(), TEXTFIELD_COLUMNS);
        addComponent(cacheSizeTextField, Label.CACHE_SIZE);

        try {
            cacheAgeTextField = new JFormattedTextField(new MaskFormatter("##:##:##:##"));
            cacheAgeTextField.setColumns(10);

            cacheAgeTextField.setText(cacheConfig.getCacheAgeAsString());
            addComponent(cacheAgeTextField, Label.CACHE_AGE);
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Provides the CacheConfig based on the panels input fields.
     *
     * @return The values from the panels input fields packed as a CacheConfig object.
     */
    @Override
    CacheConfig getValue() {
        return CacheConfig.fromStrings(cacheSizeTextField.getText(), cacheAgeTextField.getText());
    }
}
