package to.be.renamed.executable;

import to.be.renamed.bridge.EcomId;
import to.be.renamed.bridge.EcomProduct;
import to.be.renamed.fspage.FsPageEnhancer;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.model.FieldsConfig;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.script.Executable;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.agency.StoreAgent;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Bulk Enhance Product Pages", displayName = ProjectAppHelper.MODULE_NAME
                                                                                                      + " - Executable: Bulk Enhance Product Pages")
public class BulkEnhanceProductPageExecutable extends ExecutableUtilities implements Executable {

    private static final String FORM_FIELD_NAME_PARAM = "form_field_name";
    private static final String LANGUAGE_PARAM = "language";

    private FsPageEnhancer pageEnhancer = null;
    private FieldsConfig fieldsConfig = new FieldsConfig();

    @Override
    public Object execute(final Map<String, Object> parameters, final Writer out, final Writer error) {
        setParameters(parameters);
        final BaseContext context = (BaseContext) parameters.get("context");

        if (context == null) {
            Logging.logError("Missing mandatory parameter 'context'!", getClass());
        }

        final String formFieldName = requireParam(FORM_FIELD_NAME_PARAM);

        if (context != null && formFieldName != null) {
            fieldsConfig = extractFieldsConfig(context);

            pageEnhancer = Objects.requireNonNullElseGet(pageEnhancer, () -> new FsPageEnhancer(context));

            final Language language = (Language) parameters.get(LANGUAGE_PARAM);
            final List<Page>
                productPages =
                context.requireSpecialist(StoreAgent.TYPE).getStore(Store.Type.PAGESTORE).getChildren(Page.class, true).toStream()
                    .filter(this::isProductPage).toList();

            productPages.forEach(
                page -> pageEnhancer.setPdpMetaKeywords(page, language, formFieldName, getEcomProductFromPage(context, page, language)));
        }
        return null;
    }

    private boolean isProductPage(final Page page) {
        return Objects.equals(page.getFormData().get(null, fieldsConfig.getTypeField()).get(), EcomId.TYPE_PRODUCT);
    }

    private EcomProduct getEcomProductFromPage(final BaseContext context, final Page page, final Language language) {
        String productId = (String) page.getFormData().get(null, fieldsConfig.getIdField()).get();

        assert productId != null;
        final List<EcomProduct> result = ServiceFactory.getBridgeService(context).getProducts(List.of(productId), language.getAbbreviation());
        return result.get(0);
    }
}
