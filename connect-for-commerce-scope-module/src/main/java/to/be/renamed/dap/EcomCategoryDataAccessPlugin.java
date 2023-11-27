package to.be.renamed.dap;

import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.module.EcomConnectWebApp;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.module.ServiceFactory;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
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
import javax.swing.ImageIcon;

import static java.lang.String.format;

@PublicComponent(name = ProjectAppHelper.MODULE_NAME + " - Categories Data Access Plugin", displayName = ProjectAppHelper.MODULE_NAME
                                                                                                         + " - Data Access Plugin: Categories")
public class EcomCategoryDataAccessPlugin extends EcomAbstract<EcomCategory> {

    public static final String FILTER_CATEGORY = "parentId";

    @Override
    public Class<EcomCategory> getType() {
        return EcomCategory.class;
    }

    @Override
    public String getReportSvgIconPath() {
        return EcomConnectWebApp.CATEGORY_DAP_ICON;
    }

    @Override
    public ImageIcon getReportPngImageIcon() {
        return new ImageIcon(getClass().getResource("/files-web/fcecom-category.png"));
    }

    @Override
    public String getReportLabel() {
        return scope.getLabel("report.categories.label");
    }

    @Override
    public Collection<EcomDataAccessPlugin.StaticReportItem> getStaticReportItems() {
        return Collections.singletonList(new EcomDataAccessPlugin.StaticReportItem() {

            @Override
            public @NotNull String getLabel(@NotNull BaseContext context) {
                return scope.getLabel("report.categories.static.refresh");
            }

            @Override
            public void execute(@NotNull BaseContext context) {
                ServiceFactory.getBridgeService(context).invalidateCache();
                scope.openReport(context, EcomCategoryDataAccessPlugin.class);
            }
        });
    }

    @Override
    public void getReportFilter(EcomFilterBuilder filters) {
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());
        if (!bridgeService.hasCategoryTree()) {
            return;
        }

        final var projectAppConfigurationService = ServiceFactory.getProjectAppConfigurationService(scope.getBroker());
        final var projectAppConfiguration = projectAppConfigurationService.loadConfiguration();
        int categoryLevels = projectAppConfiguration.getReportConfig().getCategoryReportCategoryLevel();
        Map<String, String> tempCategoryFilter = new LinkedHashMap<>();

        Map<String, EcomCategory> categoryTree;

        try {
            categoryTree = bridgeService.getCategoriesTree(scope.getLang());
        } catch (BridgeConnectionException e) {
            Logging.logError(format(ERROR_LOG_MESSAGE, ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            openDialog(e.getLocalizedMessage(), e.getErrorCode());
            categoryTree = Collections.emptyMap();
        }
        categoryTree
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue()
                                 .getParentChain()
                                 .size() < categoryLevels
                             && !entry.getValue()
                .getChildren()
                .isEmpty())
            .forEach(entry -> {
                String
                    label =
                    String.join("", Collections.nCopies(entry.getValue().getParentChain().size(), "│ ")) + "├ " + entry.getValue().getLabel();
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

    @Override
    public List<EcomCategory> resolve(Collection<String> identifiers) {
        try {
            return ServiceFactory.getBridgeService(scope.getBroker()).getCategories(identifiers, scope.getLang());
        } catch (BridgeConnectionException e) {
            Logging.logError(format(ERROR_LOG_MESSAGE, ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            openDialog(e.getLocalizedMessage(), e.getErrorCode());
            return Collections.emptyList();
        }
    }

    @Override
    public Iterator<EcomCategory> getItems(Map<String, String> filters) {
        try {
            return ServiceFactory.getBridgeService(scope.getBroker()).findCategories(filters.get(FILTER_CATEGORY), scope.getLang()).iterator();
        } catch (BridgeConnectionException e) {
            Logging.logError(format(ERROR_LOG_MESSAGE, ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            openDialog(e.getLocalizedMessage(), e.getErrorCode());
            return Collections.emptyIterator();
        }

    }

    @Override
    public String getItemId(EcomCategory item) {
        return item.getId();
    }

    @Override
    public String getItemHeader(EcomCategory item, Language language) {
        return item.getLabel();
    }

    @Override
    public void onItemClick(ReportContext<EcomCategory> context) {
        scope.openStoreFrontUrl(context, context.getObject());
    }

    @Override
    public Collection<EcomDataAccessPlugin.ReportItemAction<EcomCategory>> getReportItemActions() {
        return Collections.singletonList(new EcomDataAccessPlugin.ReportItemAction<>() {

            @Override
            public boolean isVisible(ReportContext<EcomCategory> context) {
                return context.is(BaseContext.Env.WEBEDIT);
            }

            @Override
            public String getIconPath(ReportContext<EcomCategory> context) {
                return EcomConnectWebApp.PRODUCT_DAP_ICON;
            }

            @Override
            public @NotNull String getLabel(@NotNull ReportContext<EcomCategory> context) {
                return scope.getLabel("report.categories.action.show-products");
            }

            @Override
            public void execute(@NotNull ReportContext<EcomCategory> context) {
                Map<String, Object> params = Collections.singletonMap(EcomProductDataAccessPlugin.FILTER_CATEGORY, context.getObject().getId());
                scope.openReport(context, EcomProductDataAccessPlugin.class, params);
            }
        });
    }
}
