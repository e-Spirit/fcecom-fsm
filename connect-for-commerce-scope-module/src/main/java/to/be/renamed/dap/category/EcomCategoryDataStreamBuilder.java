package to.be.renamed.dap.category;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.EcomFilterBuilder;
import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.module.ServiceFactory;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectType;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

public class EcomCategoryDataStreamBuilder implements DataStreamBuilder<EcomCategory> {

    private final StreamBuilderAspectMap streamBuilderAspects;
    private final EcomConnectScope scope;
    private final EcomFilterBuilder filterBuilder;

    public EcomCategoryDataStreamBuilder(final EcomConnectScope scope) {
        this.scope = scope;
        streamBuilderAspects = new StreamBuilderAspectMap();

        filterBuilder = new EcomFilterBuilder();
        getReportFilter(filterBuilder);
        streamBuilderAspects.put(Filterable.TYPE, filterBuilder.asFilterable());
    }


    @Override
    public <A> A getAspect(@NotNull StreamBuilderAspectType<A> aspect) {
        return streamBuilderAspects.get(aspect);
    }

    @Override
    public @NotNull DataStream<EcomCategory> createDataStream() {
        return new EcomCategoryDataStream(scope, filterBuilder);
    }

    private void getReportFilter(EcomFilterBuilder filterBuilder) {
        filterBuilder.addTextField(EcomDapUtilities.FILTER_QUERY, scope.getLabel(scope.getDisplayLanguage(), "report.products.filter.q"));
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
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e,
                             this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
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
                                      scope.getLabel(scope.getDisplayLanguage(), "report.categories.filter.label"),
                                      tempCategoryFilter.size(),
                                      scope.getLabel(scope.getDisplayLanguage(), "report.categories.filter.of"),
                                      bridgeService.getCategoriesTree(scope.getLang()).size()));
        categoryFilter.putAll(tempCategoryFilter);
        filterBuilder.addSelect(EcomDapUtilities.FILTER_PARENT_ID, categoryFilter);
    }
}
