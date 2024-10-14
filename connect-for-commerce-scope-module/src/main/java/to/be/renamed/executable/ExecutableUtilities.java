package to.be.renamed.executable;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Long getLongParam(String parameterName) {
        if (parameters.containsKey(parameterName)) {
            Object value = parameters.get(parameterName);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
        }
        return null;
    }

    public <T> List<T> getListParam(String parameterName) {
        if (parameters.containsKey(parameterName)) {
            Object value = parameters.get(parameterName);
            if (value instanceof List) {
                return (List<T>) value;
            }
        }
        return null;
    }


    public Map<String, Object> getNestedParametersMap(String parameterName) {
        if (parameters != null && parameters.containsKey(parameterName)) {
            Object value = parameters.get(parameterName);
            if (value instanceof Map<?, ?>) {
                if (!((Map<?, ?>) value).isEmpty()) {
                    return (Map<String, Object>) value;
                }
            } else {
                throw new InvalidNestedMapTypeException(parameterName);
            }
        }
        return new HashMap<>();
    }

    public String requireParam(String parameterName) {
        String value = getParam(parameterName);
        if (value == null) {
            throw new RequiredParamMissingException(parameterName);
        }
        return value;
    }
}
