package to.be.renamed.module.projectconfig.gui;

import to.be.renamed.module.projectconfig.model.FieldsConfig;

import javax.swing.JTextField;

/**
 * Configuration panel for fields related configuration tab.
 */
public class FieldsConfigurationPanel extends AbstractConfigurationPanel<FieldsConfig> {

    private final JTextField idFieldTextField;
    private final JTextField typeFieldTextField;

    /**
     * Creates a configuration panel for the fields related configuration tab.
     *
     * @param fieldsConfig The current fields configuration values
     */
    public FieldsConfigurationPanel(FieldsConfig fieldsConfig) {
        super();
        idFieldTextField = new JTextField(fieldsConfig.getIdField(), TEXTFIELD_COLUMNS);
        addComponent(idFieldTextField, Label.FIELDS_ID_FIELD_NAME);

        typeFieldTextField = new JTextField(fieldsConfig.getTypeField(), TEXTFIELD_COLUMNS);
        addComponent(typeFieldTextField, Label.FIELDS_TYPE_FIELD_NAME);

        addDocumentationHint();
    }

    // Sonarlint does not want us to call this inside the constructor.
    private void addDocumentationHint() {
        addRichText(Label.FIELDS_DOCUMENTATION);
    }

    /**
     * Provides the FieldsConfig based on the panels input fields.
     *
     * @return The values from the panels input fields packed as a FieldsConfig object.
     */
    @Override
    FieldsConfig getValue() {
        return new FieldsConfig(idFieldTextField.getText(), typeFieldTextField.getText());
    }
}
