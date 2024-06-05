package to.be.renamed.dap.content;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.EcomContent;
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

public class EcomContentDataStream implements DataStream<EcomContent> {

    private final Iterator<EcomContent> iterator;
    private final int[] total = {0};
    private final EcomConnectScope scope;

    public EcomContentDataStream(final EcomConnectScope scope, final EcomFilterBuilder filterBuilder) {
        this.scope = scope;
        iterator = getItems(filterBuilder.getFilter());
    }


    @Override
    public @NotNull List<EcomContent> getNext(int count) {
        count = Math.max(count, 30);

        Set<EcomContent> items = new LinkedHashSet<>();
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

    private Iterator<EcomContent> getItems(Map<String, String> filters) {
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());

        try {
            return bridgeService.findContent(filters.get(EcomDapUtilities.FILTER_QUERY), scope.getLang()).iterator();
        } catch (BridgeConnectionException e) {
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
            return Collections.emptyIterator();
        }
    }
}
