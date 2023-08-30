package to.be.renamed;

import to.be.renamed.bridge.client.Json;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import to.be.renamed.error.ErrorCode;

import com.google.common.base.Strings;
import de.espirit.common.base.Logging;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.SpecialistsBroker;
import de.espirit.firstspirit.ui.operations.RequestOperation;
import de.espirit.firstspirit.webedit.server.ClientScriptOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.text.MessageFormat.format;

class EcomContentCreatorMessage {
    private static final String ERROR_CODES_BUNDLE_NAME = "ErrorCodes";
    private final Map<String, Object> message = new HashMap<>();

    EcomContentCreatorMessage(String topic) {
        add("topic", topic);
    }

    static EcomContentCreatorMessage create(String topic) {
        return new EcomContentCreatorMessage(topic);
    }

    EcomContentCreatorMessage add(String key, Object value) {
        if (value != null && (!(value instanceof String) || !((String) value).isEmpty())) {
            message.put(key, value);
        }
        return this;
    }

    EcomContentCreatorMessage add(Map<String, Object> params) {
        message.putAll(params);
        return this;
    }

    /**
     * applies the message by posting it via WE_API to the storefront, informing the storefront about events.
     *
     * @param broker
     */
    public void apply(SpecialistsBroker broker) {
        String json = Json.asJsonElement(message).toString();
        final ProjectAppConfigurationService projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(broker);
        final ProjectAppConfiguration projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
        if (!projectAppConfiguration.getGeneralConfig().useCCExtensions()) {
            String topic = message.get("topic").toString();
            String postMessageNamespace = "fcecom";
            String postMessageOrigin = "'*'";
            String javascript = String.format(
                "() => (WE_API.Preview.getWindow().postMessage( {%s: { topic: '%s', payload: %s }}, %s), 1)",
                postMessageNamespace, topic, json, postMessageOrigin
            );
            Objects.requireNonNull(broker.requireSpecialist(OperationAgent.TYPE)
                                       .getOperation(ClientScriptOperation.TYPE))
                .perform(javascript, false);
        } else {
            String loaderPath = projectAppConfiguration.getGeneralConfig().getCcExtensionsUrl();
            if (!Strings.isNullOrEmpty(loaderPath)) {
                Objects.requireNonNull(broker.requireSpecialist(OperationAgent.TYPE)
                                           .getOperation(ClientScriptOperation.TYPE))
                        .perform("() => (FCECOM_API.applyMessage(" + json + "), 1)", false);
            } else {
                String errorCode = ErrorCode.MISSING_CC_EXTENSION.get();
                EcomConnectScope scope = new EcomConnectScope(broker);
                ResourceBundle errorCodes;
                try {
                    errorCodes = ResourceBundle.getBundle(ERROR_CODES_BUNDLE_NAME, scope.getLanguage().getLocale());
                } catch (MissingResourceException e) {
                    Logging.logWarning(format("Could not find resource bundle '%s' for locale '%s', using default bundle", ERROR_CODES_BUNDLE_NAME, scope.getLanguage().getLocale().toString()), e, EcomContentCreatorMessage.class);
                    errorCodes = ResourceBundle.getBundle(ERROR_CODES_BUNDLE_NAME);
                }

                RequestOperation alert = broker.requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
                Objects.requireNonNull(alert).setKind(RequestOperation.Kind.ERROR);
                alert.setTitle("Extension not found");
                alert.perform(errorCodes.getString("errorCode") + " " + errorCode + " | " + errorCodes.getString(errorCode));
                Logging.logWarning("Missing ContentCreator Extension.", EcomContentCreatorMessage.class);
            }
        }
    }
}
