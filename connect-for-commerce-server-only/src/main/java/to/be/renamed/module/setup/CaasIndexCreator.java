package to.be.renamed.module.setup;

import com.espirit.caasconnect.service.CaasConnectService;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.ServicesBroker;
import de.espirit.firstspirit.module.ProjectEnvironment;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONException;
import kong.unirest.json.JSONObject;

import static java.lang.String.format;

/**
 * Provides a run() method which creates a find page caas index for preview and release collection.
 */
public final class CaasIndexCreator {

    private static final String ERROR_CHECKING_INDICES = "Error while checking if index already exists.";
    private static final String COULD_NOT_CREATE = "Could not create %s find page caas index: ";
    private static final String COULD_NOT_FETCH = "Could not fetch existing %s caas indices: ";
    private static final String SUCCESSFULLY_CREATED = "find page caas index successfully created.";

    private static final String FIND_PAGE_INDEX_NAME = "idx_pageType_pageId_lang_country";
    private static final String
        FIND_PAGE_INDEX =
        "{\"keys\": {\"page.formData.type.value\": 1, \"page.formData.id.value\": 1, \"locale.language\": 1, \"locale.country\": 1}}";
    private static final String PREVIEW_COLLECTION = "preview.content";
    private static final String RELEASE_COLLECTION = "release.content";

    private final ProjectEnvironment projectEnvironment;

    private CaasIndexCreator(final ProjectEnvironment projectEnvironment) {
        this.projectEnvironment = projectEnvironment;
    }

    public static CaasIndexCreator create(final ProjectEnvironment projectEnvironment) {
        return new CaasIndexCreator(projectEnvironment);
    }

    CaasConnectService getCaasConnectService() {
        return projectEnvironment.getBroker().requireSpecialist(ServicesBroker.TYPE).getService(CaasConnectService.class);
    }

    String getCaasProjectId() {
        return Objects.requireNonNull(Objects.requireNonNull(projectEnvironment.getProject()).getUuid()).toString();
    }

    String getCaasApiKey(String caasCollection) {
        Map<String, UUID>
            caasApiKeys =
            getCaasConnectService().fetchAutomatedApiKeys(
                Objects.requireNonNull(Objects.requireNonNull(projectEnvironment.getProject()).getUuid()));
        if (caasCollection.equals(PREVIEW_COLLECTION)) {
            return caasApiKeys.get("PREVIEW_READWRITE").toString();
        } else if (caasCollection.equals(RELEASE_COLLECTION)) {
            return caasApiKeys.get("RELEASE_READWRITE").toString();
        }
        return null;
    }

    String getCaaSUrl() {
        return getCaasConnectService().getConfiguration().getBaseUrl().toString();
    }

    String getCaasTenantId() {
        return getCaasConnectService().getConfiguration().getTenantId().getValue();
    }

    static HttpResponse<JsonNode> getExistingIndices(final String caasUrl, final String caasTenant, final String caasApiKey, final String caasProject,
                                                     final String caasCollection) {
        return Unirest.get(caasUrl + "/" + caasTenant + "/" + caasProject + "." + caasCollection + "/_indexes")
            .header("Authorization", "Bearer " + caasApiKey).asJson();
    }

    static HttpResponse<JsonNode> createIndex(final String caasUrl, final String caasTenant, final String caasApiKey, final String caasProject,
                                              final String caasCollection, final String indexName, final String index) {
        return Unirest.put(caasUrl + "/" + caasTenant + "/" + caasProject + "." + caasCollection + "/_indexes" + "/" + indexName)
            .header("Authorization", "Bearer " + caasApiKey).header("Content-Type", "application/json").body(index).asJson();
    }

    static void createFindPageIndexIfNeeded(String caasUrl, String caasTenant, String caasApiKey, String caasProject, String caasCollection) {
        HttpResponse<JsonNode> response = getExistingIndices(caasUrl, caasTenant, caasApiKey, caasProject, caasCollection);
        try {
            if (response.isSuccess()) {
                if (!hasFindPageIndex(response.getBody())) {
                    HttpResponse<JsonNode>
                        indexCreateResponse =
                        createIndex(caasUrl, caasTenant, caasApiKey, caasProject, caasCollection, FIND_PAGE_INDEX_NAME, FIND_PAGE_INDEX);
                    if (indexCreateResponse.isSuccess()) {
                        Logging.logDebug(caasCollection + " " + SUCCESSFULLY_CREATED, CaasIndexCreator.class);
                    } else {
                        Logging.logError(format(COULD_NOT_CREATE, caasCollection) + indexCreateResponse.getStatus(), CaasIndexCreator.class);
                    }
                }
            } else {
                Logging.logError(format(COULD_NOT_FETCH, caasCollection) + response.getStatus(), CaasIndexCreator.class);
            }
        } catch (CaasIndexCreatorException e) {
            Logging.logError(format(COULD_NOT_CREATE, caasCollection), e, CaasIndexCreator.class);
        }
    }

    static boolean hasFindPageIndex(JsonNode existingIndices) {
        try {
            JSONObject embedded = existingIndices.getObject().getJSONObject("_embedded");
            JSONArray indices = embedded.getJSONArray("rh:index");

            return indices.toList().stream().anyMatch(entry -> {
                try {
                    return ((JSONObject) entry).get("_id").equals(FIND_PAGE_INDEX_NAME);
                } catch (JSONException e) {
                    Logging.logError("Error accessing '_id' property: " + e.getMessage(), CaasIndexCreator.class);
                    throw new CaasIndexCreatorException(ERROR_CHECKING_INDICES, e);
                }
            });
        } catch (JSONException e) {
            Logging.logError("Error accessing JSON structure: ", e, CaasIndexCreator.class);
            throw new CaasIndexCreatorException(ERROR_CHECKING_INDICES, e);
        }
    }

    /**
     * Creates a find page caas index for preview and release collection.
     */
    public void run() {
        createFindPageIndexIfNeeded(getCaaSUrl(), getCaasTenantId(), getCaasApiKey(PREVIEW_COLLECTION), getCaasProjectId(), PREVIEW_COLLECTION);
        createFindPageIndexIfNeeded(getCaaSUrl(), getCaasTenantId(), getCaasApiKey(RELEASE_COLLECTION), getCaasProjectId(), RELEASE_COLLECTION);
    }
}
