package to.be.renamed.dap.content;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.EcomContent;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.content.aspects.EcomContentDataTemplating;
import to.be.renamed.dap.content.aspects.EcomContentJsonSupporting;
import to.be.renamed.dap.content.aspects.EcomContentTransferHandling;
import to.be.renamed.dap.content.aspects.EcomContentTransferSupplying;
import to.be.renamed.error.BridgeConnectionException;
import to.be.renamed.module.ServiceFactory;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.editor.ValueIndexer;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ValueIndexing;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static java.lang.String.format;

public class EcomContentDataAccessSession implements DataAccessSession<EcomContent> {

    private final BaseContext baseContext;
    private final EcomConnectScope scope;
    private final SessionAspectMap sessionAspects;

    public EcomContentDataAccessSession(final BaseContext baseContext, final EcomConnectScope scope) {
        this.baseContext = baseContext;
        this.scope = scope;

        sessionAspects = new SessionAspectMap();
        sessionAspects.put(TransferHandling.TYPE, new EcomContentTransferHandling(baseContext));
        sessionAspects.put(TransferSupplying.TYPE, new EcomContentTransferSupplying(baseContext));
        sessionAspects.put(ValueIndexing.TYPE, (identifier, language, recursive, indexer) ->
            indexer.append(ValueIndexer.VALUE_FIELD, identifier));
        sessionAspects.put(DataTemplating.TYPE, new EcomContentDataTemplating());
        sessionAspects.put(JsonSupporting.TYPE, new EcomContentJsonSupporting());
    }

    @Override
    public <A> A getAspect(final @NotNull SessionAspectType<A> sessionAspectType) {
        return sessionAspects.get(sessionAspectType);
    }

    @Override
    public @NotNull EcomContent getData(@NotNull String identifier) throws NoSuchElementException {
        List<EcomContent> data = getData(Collections.singletonList(identifier));
        if (data.isEmpty()) {
            throw new NoSuchElementException("Could not resolve id: " + identifier);
        }
        return data.get(0);
    }

    @Override
    public @NotNull List<EcomContent> getData(@NotNull Collection<String> identifiers) {
        return resolve(identifiers);
    }

    @Override
    public @NotNull String getIdentifier(@NotNull EcomContent item) throws NoSuchElementException {
        return item.getId();
    }

    @Override
    public @NotNull DataSnippetProvider<EcomContent> createDataSnippetProvider() {
        return new EcomContentDataSnippetProvider(baseContext);
    }

    @Override
    public @NotNull DataStreamBuilder<EcomContent> createDataStreamBuilder() {
        return new EcomContentDataStreamBuilder(scope);
    }

    public List<EcomContent> resolve(Collection<String> identifiers) {
        BridgeService bridgeService = ServiceFactory.getBridgeService(scope.getBroker());

        try {
            return bridgeService.getContent(identifiers, scope.getLang());
        } catch (BridgeConnectionException e) {
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
            return Collections.emptyList();
        }
    }
}


