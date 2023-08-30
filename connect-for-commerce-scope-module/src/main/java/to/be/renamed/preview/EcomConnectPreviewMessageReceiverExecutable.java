package to.be.renamed.preview;

import to.be.renamed.executable.ExecutableUtilities;
import to.be.renamed.error.CreatePageException;
import to.be.renamed.EcomConnectException;
import to.be.renamed.EcomConnectScope;
import to.be.renamed.OrphanedPageRefException;
import to.be.renamed.bridge.EcomContent;
import to.be.renamed.bridge.EcomId;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.access.ProjectAppConfigurationService;
import to.be.renamed.module.projectconfig.model.ProjectAppConfiguration;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.access.store.IDProvider;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.agency.LanguageAgent;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.ui.operations.RequestOperation;

import java.io.Writer;
import java.util.Map;

import static java.lang.String.format;

import static to.be.renamed.error.ErrorUtils.prettyErrorString;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Preview Message Receiver", displayName = ProjectAppHelper.MODULE_NAME + " - Executable: Preview Message Receiver")
public class EcomConnectPreviewMessageReceiverExecutable extends ExecutableUtilities implements Executable {

    private static final String TOPIC_PARAM = "topic";
    private static final String STOREFRONT_URL_PARAM = "url";
    private static final String PAGE_REF_ID_PARAM = "pageRefId";
    private static final String LANGUAGE_PARAM = "language";

    private static final String CREATE_PAGE_VIA_BRIDGE_DEACTIVATED = "Did not ensure existence of page with uid '%s' in shop system. Create page via bridge is disabled in the project config.";

    private BaseContext context;
    private EcomConnectScope scope;

    @Override
    public Object execute(Map<String, Object> parameters, Writer out, Writer err) {
        setParameters(parameters);
        context = (BaseContext) parameters.get("context");

        try {
            String topic = requireParam(TOPIC_PARAM);
            scope = new EcomConnectScope(context);
            switch (topic) {
                case "updatedStoreFrontUrl":
                    updatedStoreFrontUrl(requireParam(STOREFRONT_URL_PARAM));
                    break;
                case "requestedPreviewElement":
                    requestedPreviewElement(requireParam(PAGE_REF_ID_PARAM), getParam(LANGUAGE_PARAM));
                    break;
                default:
                    context.logError("Couldn't handle '" + topic + "'-topic. parameters=" + parameters);
            }
        } catch (CreatePageException e) {
            context.logError(e.getMessage(), e);
            RequestOperation alert = context.requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
            alert.setKind(RequestOperation.Kind.ERROR);
            alert.setTitle("Error during page creation");
            alert.perform(prettyErrorString(e.getBridgeErrors(), scope.getLanguage().getLocale()));
        } catch (Exception e) {
            context.logError(e.getMessage(), e);
            RequestOperation alert = context.requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
            alert.setKind(RequestOperation.Kind.ERROR);
            alert.setTitle("Error during page creation");
            alert.perform(e.getLocalizedMessage());
        }
        return null;
    }



    private void updatedStoreFrontUrl(String storeFrontUrl) {
        EcomId ecomId = ServiceFactory.getBridgeService(scope.getBroker()).resolveStoreFrontUrl(storeFrontUrl);
        if (ecomId != null) {
            scope.setContentCreatorPreviewElement(context, ecomId);
        }
    }

    private void requestedPreviewElement(String pageRefId, String languageAbbr) throws EcomConnectException, OrphanedPageRefException {
        IDProvider element = context.requireSpecialist(StoreAgent.TYPE)
            .getStore(Store.Type.SITESTORE)
            .getStoreElement(Long.parseLong(pageRefId));

        if (element instanceof PageRef) {
            Language language = context.requireSpecialist(LanguageAgent.TYPE).getProjectLanguages(false).get(languageAbbr);
            EcomId ecomId = EcomId.from((PageRef) element, language != null ? language : scope.getLanguage());
            if (ecomId instanceof EcomContent) {
                final ProjectAppConfigurationService projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(context);
                final ProjectAppConfiguration projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
                if (projectAppConfiguration.getGeneralConfig().disableBridgePageCreation()) {
                    Logging.logDebug(format(CREATE_PAGE_VIA_BRIDGE_DEACTIVATED, element.getUid()), getClass());
                } else {
                    ((EcomContent) ecomId).ensureExistence(scope);
                }
            }
            scope.openStoreFrontUrl(context, ecomId);
        }
    }
}
