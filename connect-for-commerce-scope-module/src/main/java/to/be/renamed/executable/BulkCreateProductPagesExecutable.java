package to.be.renamed.executable;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomProduct;
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

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Bulk Create Product Pages", displayName = ProjectAppHelper.MODULE_NAME + " - Executable: Bulk Create Product Pages")
public class BulkCreateProductPagesExecutable extends ExecutableUtilities implements Executable {

    protected static final String PARAM_SUBCATEGORY = "bulk_product_page_creation_subcategory";
    private static final String PARAM_SUBFOLDER = "bulk_product_page_creation_subfolder";
    protected static final String PARAM_PAGE_TEMPLATE = "bulk_product_page_creation_template";
    private static final String PARAM_PAGE_RELEASE = "bulk_product_page_creation_release";

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
            final String subfolder = getParam(PARAM_SUBFOLDER);
            final boolean release = Boolean.parseBoolean(getParam(PARAM_PAGE_RELEASE));

            final EcomConnectScope scope = new EcomConnectScope(baseContext);

            EcomSearchResult<EcomProduct> results = new EcomSearchResult<>(Collections.emptyList(), 0);
            int count = 0;
            int page = 0;

            do {
                try {
                    results = ServiceFactory.getBridgeService(scope.getBroker())
                        .findProducts("", subcategory, scope.getLang(), ++page);
                } catch (BridgeConnectionException e) {
                    Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e,
                                     this.getClass());
                    break;
                }

                Logging.logInfo("Found " + results.getTotal() + " products for bulk page creation.", getClass());
                if (results.getTotal() == 0 && subcategory == null) {
                    Logging.logInfo("This can be caused by a missing '" + PARAM_SUBCATEGORY + "' parameter.", getClass());
                }

                for (Iterator<EcomProduct> it = results.getIterator(); it.hasNext(); ) {
                    count++;
                    final EcomProduct product = it.next();
                    Logging.logInfo("Creating page for product (" + count + "): " + product.getId() + " / " + product.getLabel(), getClass());

                    final Map<String, Object> displayNames = new HashMap<>();
                    LanguageAgent languageAgent = scope.getBroker().requireSpecialist(LanguageAgent.TYPE);
                    for (Language language : languageAgent.getProjectLanguages(false).values()) {
                        displayNames.put(language.getAbbreviation(), product.getLabel());
                    }

                    FsPageCreator fsPageCreator = Objects.requireNonNullElseGet(pageCreator, () -> new FsPageCreator(scope));
                    fsPageCreator.create(product.getId(), fsPageTemplate, FsPageCreator.PRODUCT_PAGE_TYPE, displayNames, subfolder, release, true);
                }
            } while (count < results.getTotal());
        }
        return null;
    }
}
