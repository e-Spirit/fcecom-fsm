package to.be.renamed.dap.product;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomProduct;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.EcomFilterBuilder;
import to.be.renamed.error.BridgeConnectionException;
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

    private final Iterator<EcomProduct> iterator;
    private final int[] total = {0};
    private final EcomConnectScope scope;

    public EcomProductDataStream(final EcomConnectScope scope, final EcomFilterBuilder filterBuilder) {
        this.scope = scope;
        iterator = getItems(filterBuilder.getFilter());
    }


    @Override
    public @NotNull List<EcomProduct> getNext(int count) {
        count = Math.max(count, 30);

        Set<EcomProduct> items = new LinkedHashSet<>();
        while (count >= 1 && iterator.hasNext()) {
            items.add(iterator.next());
            count--;
        }

        total[0] += items.size();
        return new ArrayList<>(items);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public int getTotal() {
        return hasNext() ? -1 : total[0];
    }

    @Override
    public void close() {
        // No need to close the Iterator
    }

    private Iterator<EcomProduct> getItems(Map<String, String> filters) {
        try {
            return ServiceFactory.getBridgeService(scope.getBroker())
                .findProducts(filters.get(EcomDapUtilities.FILTER_QUERY), filters.get(EcomDapUtilities.FILTER_CATEGORY), scope.getLang()).iterator();
        } catch (BridgeConnectionException e) {
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
            return Collections.emptyIterator();
        }
    }
}
