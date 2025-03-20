package to.be.renamed.dap.product;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomId;
import to.be.renamed.bridge.EcomProduct;
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

public class EcomProductDataStream implements DataStream<EcomProduct> {

    private final EcomConnectScope scope;
    private final Map<String, String> filter;
    private final int total;
    private int page = 1;
    private int count = 0;
    private Iterator<EcomProduct> iterator;


    public EcomProductDataStream(final EcomConnectScope scope, final EcomFilterBuilder filterBuilder) {
        this.scope = scope;
        this.filter = filterBuilder.getFilter();
        EcomSearchResult<EcomProduct> searchResult = getItems(filter, page);
        iterator = searchResult.getIterator();
        total = searchResult.getTotal();
    }


    @Override
    public @NotNull List<EcomProduct> getNext(int count) {
        Set<EcomProduct> items = new LinkedHashSet<>();
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

    private EcomSearchResult<EcomProduct> getItems(Map<String, String> filters, int page) {
        try {
            return EcomDapUtilities.applyManagedFlag(
                ServiceFactory.getBridgeService(scope.getBroker())
                    .findProducts(filters.get(EcomDapUtilities.FILTER_QUERY), filters.get(EcomDapUtilities.FILTER_CATEGORY), scope.getLang(), page),
                EcomId.TYPE_PRODUCT, scope);
        } catch (BridgeConnectionException e) {
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e,
                             this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
            return new EcomSearchResult<>(Collections.emptyList(), 0);
        }
    }
}
