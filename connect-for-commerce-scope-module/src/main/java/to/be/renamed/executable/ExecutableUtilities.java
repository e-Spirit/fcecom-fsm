package to.be.renamed.executable;

import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.model.FieldsConfig;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.BrokerAgent;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ExecutableUtilities {

    private Map<String, Object> parameters;

    public FieldsConfig extractFieldsConfig(final BaseContext context) {
        final long projectId = context.requireSpecialist(ProjectAgent.TYPE).getId();
        SpecialistsBroker broker = context.requireSpecialist(BrokerAgent.TYPE).getBrokerByProjectId(projectId);
        return ServiceFactory.getProjectAppConfigurationService(broker).loadConfiguration().getFieldsConfig();
    }

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

    @Nullable
    public Long getLongParam(String parameterName) {
        if (parameters.containsKey(parameterName) &&
            parameters.get(parameterName) instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    public long getLongParam(String parameterName, long defaultValue) {
        return Optional.ofNullable(getLongParam(parameterName)).orElse(defaultValue);
    }

    @Nullable
    public Boolean getBooleanParam(String parameterName) {
        if (parameters.containsKey(parameterName)) {
            Object value = parameters.get(parameterName);
            if (value instanceof Boolean) {
                return (Boolean) value;
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
