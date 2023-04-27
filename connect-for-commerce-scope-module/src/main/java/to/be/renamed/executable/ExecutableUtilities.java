package to.be.renamed.executable;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public abstract class ExecutableUtilities {

    private Map<String, Object> parameters;

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Nullable
    public String getParam(String parameterName) {
        if (parameters.containsKey(parameterName)) {
            Object value = parameters.get(parameterName);
            if (value instanceof String && !((String) value).isEmpty()) {
                return (String) value;
            }
        }
        return null;
    }

    public Map<String, Object> getNestedParametersMap(String parameterName) {
        if (parameters != null && parameters.containsKey(parameterName)) {
            Object value = parameters.get(parameterName);
            if (value instanceof Map<?, ?>) {
                if (!((Map<?, ?>) value).isEmpty())
                    return (Map<String, Object>) value;
            } else throw new InvalidNestedMapTypeException(format("Nested parameter %s is not a map", parameterName));
        }
        return new HashMap<>();
    }

    public String requireParam(String parameterName) {
        String value = getParam(parameterName);
        if (value == null) throw new RequiredParamMissingException(format("Parameter is empty or missing '%s'", parameterName));
        return value;
    }
}
