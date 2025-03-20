package to.be.renamed.module.projectconfig.model;

import java.io.Serializable;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Representation of the report configuration.
 */
public class FieldsConfig implements Serializable {

    public static final String DEFAULT_ID_FIELD = "id";
    public static final String DEFAULT_TYPE_FIELD = "type";

    private static final long serialVersionUID = -177047415205831938L;

    private final String idField;
    private final String typeField;

    /**
     * Required by Gson.
     * Creates a report configuration with default values.
     * (idField = "id" , typeField = "type")
     */
    public FieldsConfig() {
        this(DEFAULT_ID_FIELD, DEFAULT_TYPE_FIELD);
    }

    /**
     * Creates a field names' configuration.
     *
     * @param idField   Field to save the shop backends' ID.
     * @param typeField Field to identify the type of page in FirstSpirit.
     */
    public FieldsConfig(final String idField, final String typeField) {
        this.idField = isValidFieldName(idField) ? idField : DEFAULT_ID_FIELD;
        this.typeField = isValidFieldName(typeField) ? typeField : DEFAULT_TYPE_FIELD;
    }

    private boolean isValidFieldName(String fieldName) {
        return fieldName != null && !fieldName.isBlank();
    }

    public String getIdField() {
        return idField;
    }

    public String getTypeField() {
        return typeField;
    }

    @Override
    public String toString() {
        return format("{idField: %s, typeField: %s}", idField, typeField);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FieldsConfig that = (FieldsConfig) o;
        return Objects.equals(getIdField(), that.getIdField()) &&
               Objects.equals(getTypeField(), that.getTypeField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdField(), getTypeField());
    }
}
