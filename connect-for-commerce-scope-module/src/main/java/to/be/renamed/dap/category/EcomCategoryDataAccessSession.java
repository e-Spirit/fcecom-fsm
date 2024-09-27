package to.be.renamed.dap.category;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.bridge.EcomId;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.category.aspects.EcomCategoryDataTemplating;
import to.be.renamed.dap.category.aspects.EcomCategoryJsonSupporting;
import to.be.renamed.dap.category.aspects.EcomCategoryTransferHandling;
import to.be.renamed.dap.category.aspects.EcomCategoryTransferSupplying;
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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class EcomCategoryDataAccessSession implements DataAccessSession<EcomCategory> {

    private final BaseContext baseContext;
    private final EcomConnectScope scope;
    private final SessionAspectMap sessionAspects;

    public EcomCategoryDataAccessSession(final BaseContext baseContext, final EcomConnectScope scope) {
        this.baseContext = baseContext;
        this.scope = scope;

        sessionAspects = new SessionAspectMap();
        sessionAspects.put(TransferHandling.TYPE, new EcomCategoryTransferHandling(baseContext));
        sessionAspects.put(TransferSupplying.TYPE, new EcomCategoryTransferSupplying(baseContext));
        sessionAspects.put(ValueIndexing.TYPE, (identifier, language, recursive, indexer) ->
            indexer.append(ValueIndexer.VALUE_FIELD, identifier));
        sessionAspects.put(DataTemplating.TYPE, new EcomCategoryDataTemplating());
        sessionAspects.put(JsonSupporting.TYPE, new EcomCategoryJsonSupporting());
    }

    @Override
    public <A> A getAspect(final @NotNull SessionAspectType<A> sessionAspectType) {
        return sessionAspects.get(sessionAspectType);
    }

    @Override
    public @NotNull EcomCategory getData(@NotNull String identifier) throws NoSuchElementException {
        List<EcomCategory> data = getData(Collections.singletonList(identifier));
        if (data.get(0) == null) {
            throw new NoSuchElementException("Could not resolve id: " + identifier);
        }
        return data.get(0);
    }

    @Override
    public @NotNull List<EcomCategory> getData(@NotNull Collection<String> identifiers) {
        final List<EcomCategory> resolvedCategories = resolve(identifiers);
        final Map<String, EcomCategory> categoriesMap = resolvedCategories.stream().collect(Collectors.toMap(EcomId::getId, category -> category));
        return identifiers.stream().map(identifier -> categoriesMap.getOrDefault(identifier, null)).collect(Collectors.toList());
    }

    @Override
    public @NotNull String getIdentifier(@NotNull EcomCategory item) throws NoSuchElementException {
        return item.getId();
    }

    @Override
    public @NotNull DataSnippetProvider<EcomCategory> createDataSnippetProvider() {
        return new EcomCategoryDataSnippetProvider(baseContext);
    }

    @Override
    public @NotNull DataStreamBuilder<EcomCategory> createDataStreamBuilder() {
        return new EcomCategoryDataStreamBuilder(scope);
    }

    public List<EcomCategory> resolve(Collection<String> identifiers) {
        try {
            return ServiceFactory.getBridgeService(scope.getBroker()).getCategories(identifiers, scope.getLang());
        } catch (BridgeConnectionException e) {
            Logging.logError(format(EcomDapUtilities.ERROR_LOG_MESSAGE, EcomDapUtilities.ERROR_BRIDGE_CONNECTION, e.getErrorCode()), e, this.getClass());
            EcomDapUtilities.openDialog(e.getLocalizedMessage(), e.getErrorCode(), scope);
            return Collections.emptyList();
        }
    }
}


