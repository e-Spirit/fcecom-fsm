package to.be.renamed.executable;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomId;
import to.be.renamed.bridge.client.Json;
import to.be.renamed.error.CreatePageException;
import to.be.renamed.error.ErrorCode;
import to.be.renamed.fspage.FsPageCreator;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import com.espirit.moddev.components.annotations.PublicComponent;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.access.store.sitestore.PageRef;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.ui.operations.RequestOperation;

import java.io.Writer;
import java.util.Map;
import java.util.Objects;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Create Reference Page", displayName = ProjectAppHelper.MODULE_NAME + " - Executable: Create Reference Page")
public class CreateReferencePageExecutable extends ExecutableUtilities implements Executable {

    private static final String STOREFRONT_URL_PARAM = "storeFrontUrl";
    private static final String ID_PARAM = "id";
    private static final String FS_PAGE_TEMPLATE_PARAM = "fsPageTemplate";
    private static final String PAGETYPE_PARAM = "type";
    private static final String DISPLAYNAMES_PARAM = "displayNames";

    private static final String SUCCESS_PROPERTY = "success";
    private static final String ERROR_PROPERTY = "error";
    private static final String ERROR_CODE_PROPERTY = "code";
    private static final String ERROR_CAUSE_PROPERTY = "cause";

    private BaseContext context;

    public Object execute(Map<String, Object> parameters, Writer out, Writer err) {
        setParameters(parameters);
        createContext(parameters);
        return createReferencePage();
    }

    private void createContext(Map<String, Object> parameters) {
        context = (BaseContext) parameters.get("context");
        context.logInfo("Received a request to create a reference page");
    }

    private String createReferencePage() {
        try {
            EcomConnectScope scope = EcomConnectScope.create(context);

            final String storeFrontUrl = getParam(STOREFRONT_URL_PARAM);
            final String id = requireParam(ID_PARAM);
            final String fsPageTemplate = requireParam(FS_PAGE_TEMPLATE_PARAM);
            final String pageType = requireParam(PAGETYPE_PARAM);

            if (storeFrontUrl != null) {
                EcomId ecomId = ServiceFactory.getBridgeService(context).resolveStoreFrontUrl(storeFrontUrl);
                ecomId.getElement(scope).create(getParam("template"));
                scope.setContentCreatorPreviewElement(context, ecomId);

                return createJsonResponse(true, null, null);
            } else {
                final Map<String, Object> displayNames = getNestedParametersMap(DISPLAYNAMES_PARAM);

                context.logInfo("Creating page for id: " + id);
                PageRef pageRef = new FsPageCreator(scope).create(id, fsPageTemplate, pageType, displayNames);
                if (pageRef != null) {
                    scope.setContentCreatorPreviewElement(context, id, pageType, pageRef);
                    return createJsonResponse(true, null, null);
                }
            }
        } catch (CreatePageException e) {
            context.logError(e.getMessage(), e);
            return createJsonResponse(false, e.getErrorCode(), e.getMessage());
        } catch (InvalidNestedMapTypeException e) {
            context.logError(e.getMessage(), e);
            return createJsonResponse(false, ErrorCode.INVALID_DISPLAYNAMES_FORMAT.get(), e.getMessage());
        } catch (RequiredParamMissingException e) {
            context.logError(e.getMessage(), e);
            return createJsonResponse(false, ErrorCode.REQUIRED_PARAM_MISSING.get(), e.getMessage());
        } catch (Exception e) {
            // Show error message in Content Creator
            context.logError(e.getMessage(), e);
            RequestOperation alert = context.requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
            Objects.requireNonNull(alert).setKind(RequestOperation.Kind.ERROR);
            alert.setTitle("Unknown error during page creation");
            alert.perform(e.getLocalizedMessage());

            return createJsonResponse(false, ErrorCode.UNKNOWN.get(), null);
        }

        return null;
    }

    private String createJsonResponse(Boolean success, String errorCode, String errorCause) {
        JsonObject result = new JsonObject();
        JsonObject error = new JsonObject();
        result.add(SUCCESS_PROPERTY, new JsonPrimitive(success));
        if (errorCode != null) {
            error.add(ERROR_CODE_PROPERTY, new JsonPrimitive(errorCode));
        } else {
            error.add(ERROR_CODE_PROPERTY, JsonNull.INSTANCE);
        }
        if (errorCause != null) {
            error.add(ERROR_CAUSE_PROPERTY, new JsonPrimitive(errorCause));
        } else {
            error.add(ERROR_CAUSE_PROPERTY, JsonNull.INSTANCE);
        }
        if (!(errorCode == null && errorCause == null)) {
            result.add(ERROR_PROPERTY, error);
        }

        return new Json(result).toString();
    }
}
