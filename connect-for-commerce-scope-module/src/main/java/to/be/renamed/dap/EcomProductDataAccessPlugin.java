package to.be.renamed.dap;

import to.be.renamed.module.EcomConnectWebApp;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.EcomProduct;
import to.be.renamed.module.ServiceFactory;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.client.plugin.report.ReportContext;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Products Data Access Plugin", displayName = ProjectAppHelper.MODULE_NAME + " - Data Access Plugin: Products")
public class EcomProductDataAccessPlugin extends EcomAbstract<EcomProduct> {

    public static final String FILTER_QUERY = "q";
    public static final String FILTER_CATEGORY = "categoryId";

    @Override
    public Class<EcomProduct> getType() {
        return EcomProduct.class;
    }

    @Override
    public String getReportSvgIconPath() {
        return EcomConnectWebApp.PRODUCT_DAP_ICON;
    }

    @Override
    public String getReportLabel() {
        return scope.getLabel("report.products.label");
    }

    @Override
    public Collection<EcomDataAccessPlugin.StaticReportItem> getStaticReportItems() {
        return Collections.singletonList(new EcomDataAccessPlugin.StaticReportItem() {

            @Override
            public @NotNull String getLabel(@NotNull BaseContext context) {
                return scope.getLabel("report.products.static.refresh");
            }

            @Override
            public void execute(@NotNull BaseContext context) {
                ServiceFactory.getBridgeService(context).invalidateCache();
                scope.openReport(context, EcomProductDataAccessPlugin.class);
            }
        });
    }

    @Override
    public void getReportFilter(EcomFilterBuilder filters) {
        filters.addTextField(FILTER_QUERY, scope.getLabel("report.products.filter.q"));
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());

        if (bridgeService.hasCategoryTree()) {
            final var projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(scope.getBroker());
            final var projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
            int categoryLevels = projectAppConfiguration.getReportConfig().getProductReportCategoryLevel();
            Map<String, String> tempCategoryFilter = new LinkedHashMap<>();
            bridgeService.getCategoriesTree(scope.getLang())
                                            .entrySet()
                                            .stream()
                                            .filter(entry -> entry.getValue()
                                                                  .getParentChain()
                                                                  .size() < categoryLevels)
                                            .forEach((entry) -> {
                                                String label = String.join("", Collections.nCopies(entry.getValue().getParentChain().size(), "│ ")) + "├ " + entry.getValue().getLabel();
                                                tempCategoryFilter.put(entry.getKey(), label);
                                            });

            Map<String, String> categoryFilter = new LinkedHashMap<>();
            categoryFilter.put("", format("%s (%s %s %s)",
                                    scope.getLabel("report.categories.filter.label"),
                                    tempCategoryFilter.size(),
                                    scope.getLabel("report.categories.filter.of"),
                                    bridgeService.getCategoriesTree(scope.getLang()).size()));
            categoryFilter.putAll(tempCategoryFilter);
            filters.addSelect(FILTER_CATEGORY, categoryFilter);
        }
    }

    @Override
    public List<EcomProduct> resolve(Collection<String> identifiers) {
        return ServiceFactory.getBridgeService(scope.getBroker()).getProducts(identifiers, scope.getLang());
    }

    @Override
    public Iterator<EcomProduct> getItems(Map<String, String> filters) {
        return ServiceFactory.getBridgeService(scope.getBroker()).findProducts(filters.get(FILTER_QUERY), filters.get(FILTER_CATEGORY), scope.getLang()).iterator();
    }

    @Override
    public String getItemId(EcomProduct item) {
        return item.getId();
    }

    @Override
    public String getItemHeader(EcomProduct item, Language language) {
        return item.getLabel();
    }

    @Override
    public String getItemExtract(EcomProduct item, Language language) {
        return item.getExtract();
    }

    @Override
    public String getItemImageUrl(EcomProduct item, Language language) {
        return item.getThumbnail();
    }

    @Override
    public String getItemFlyoutHtml(EcomProduct item, Language language) {
        String image = item.getImage();
        return Strings.notEmpty(image) ? format("<img src=\"%s\" style=\"width: 30vw;\" />", image) : null;
    }

    @Override
    public void onItemClick(ReportContext<EcomProduct> context) {
        scope.openStoreFrontUrl(context, context.getObject());
    }
}
