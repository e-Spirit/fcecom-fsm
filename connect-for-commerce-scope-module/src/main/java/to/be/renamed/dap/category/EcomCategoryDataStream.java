package to.be.renamed.dap.category;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.bridge.EcomId;
import to.be.renamed.bridge.EcomSearchResult;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.EcomFilterBuilder;
import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.fspage.FsPageCreator;
import to.be.renamed.module.ServiceFactory;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class EcomCategoryDataStream implements DataStream<EcomCategory> {

    private final EcomConnectScope scope;
    private final Map<String, String> filter;
    private int total;
    private int page = 1;
    private int count = 0;
    private Iterator<EcomCategory> iterator;

    public EcomCategoryDataStream(final EcomConnectScope scope, final EcomFilterBuilder filterBuilder) {
        this.scope = scope;
        this.filter = filterBuilder.getFilter();
        EcomSearchResult<EcomCategory> searchResult = getItems(filter, page);
        iterator = searchResult.getIterator();
        total = searchResult.getTotal();
    }


    @Override
    public @NotNull List<EcomCategory> getNext(int count) {
        Set<EcomCategory> items = new LinkedHashSet<>();
        if (!iterator.hasNext() && this.hasNext()) {
            page++;
            iterator = getItems(filter, page).getIterator();
        }
        while (iterator.hasNext()) {
            items.add(iterator.next());
            this.count++;
        }

        return new ArrayList<>(items);
    }

    @Override
    public boolean hasNext() {
        return count < getTotal();
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public void close() {
        // No need to close the Iterator
    }

    private EcomSearchResult<EcomCategory> getItems(Map<String, String> filters, int page) {
        try {
            return EcomDapUtilities.applyManagedFlag(
                ServiceFactory.getBridgeService(scope.getBroker())
                    .findCategories(filters.get(EcomDapUtilities.FILTER_QUERY), filters.get(EcomDapUtilities.FILTER_PARENT_ID), scope.getLang(),
                                    page), EcomId.TYPE_CATEGORY, scope);
        } catch (BridgeConnectionException e) {
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e,
                             this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
            return new EcomSearchResult<>(Collections.emptyList(), 0);
        }
    }
}
