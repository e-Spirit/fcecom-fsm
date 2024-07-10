package to.be.renamed.executable;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.bridge.EcomSearchResult;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.fspage.FsPageCreator;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.agency.LanguageAgent;

import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Bulk Create Category Pages", displayName = ProjectAppHelper.MODULE_NAME + " - Executable: Bulk Create Category Pages")
public class BulkCreateCategoryPagesExecutable extends ExecutableUtilities implements Executable {

    private static final String PARAM_SUBCATEGORY = "bulk_category_page_creation_subcategory";
    private static final String PARAM_SUBFOLDER = "bulk_category_page_creation_subfolder";
    protected static final String PARAM_PAGE_TEMPLATE = "bulk_category_page_creation_template";
    private static final String PARAM_PAGE_RELEASE = "bulk_category_page_creation_release";

    private FsPageCreator pageCreator = null;

    protected void setPageCreator(FsPageCreator pageCreator) {
        this.pageCreator = pageCreator;
    }

    @Override
    public Object execute(final Map<String, Object> parameters, final Writer out, final Writer err) {
        setParameters(parameters);
        final BaseContext baseContext = (BaseContext) parameters.get("context");
        if (baseContext == null) {
            Logging.logError("Missing mandatory parameter 'context'!", getClass());
        }
        final String fsPageTemplate = getParam(PARAM_PAGE_TEMPLATE);
        if (fsPageTemplate == null) {
            Logging.logError("Missing mandatory parameter '" + PARAM_PAGE_TEMPLATE + "'!", getClass());
        }

        if (baseContext != null && fsPageTemplate != null) {
            final String subcategory = getParam(PARAM_SUBCATEGORY);
            final String folder = getParam(PARAM_SUBFOLDER);
            final boolean release = Boolean.parseBoolean(getParam(PARAM_PAGE_RELEASE));

            final EcomConnectScope scope = new EcomConnectScope(baseContext);

            EcomSearchResult<EcomCategory> results = new EcomSearchResult<>(Collections.emptyList(), 0);
            int count = 0;
            int page = 0;

            do {
                try {
                    results = ServiceFactory.getBridgeService(scope.getBroker())
                        .findCategories("", subcategory, scope.getLang(), ++page);
                } catch (BridgeConnectionException e) {
                    Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e,
                                     this.getClass());
                    break;
                }

                Logging.logInfo("Found " + results.getTotal() + " categories for bulk page creation.", getClass());

                for (Iterator<EcomCategory> it = results.getIterator(); it.hasNext(); ) {
                    count++;
                    final EcomCategory category = it.next();
                    Logging.logInfo("Creating page for category (" + count + "): " + category.getId() + " / " + category.getLabel(), getClass());

                    final Map<String, Object> displayNames = new HashMap<>();
                    LanguageAgent languageAgent = scope.getBroker().requireSpecialist(LanguageAgent.TYPE);
                    for (Language language : languageAgent.getProjectLanguages(false).values()) {
                        displayNames.put(language.getAbbreviation(), category.getLabel());
                    }

                    FsPageCreator fsPageCreator = Objects.requireNonNullElseGet(pageCreator, () -> new FsPageCreator(scope));
                    fsPageCreator.create(category.getId(), fsPageTemplate, FsPageCreator.CATEGORY_PAGE_TYPE, displayNames, folder, release, true);
                }
            } while (count < results.getTotal());
        }
        return null;
    }
}
