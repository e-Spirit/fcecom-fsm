package to.be.renamed.dap;

import to.be.renamed.bridge.EcomId;
import to.be.renamed.bridge.client.Json;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.EcomConnectScope;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.editor.ValueIndexer;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.ProjectAgent;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessPlugin;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataAccessAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ReportItemsProviding;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Reporting;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StaticItemsProviding;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectType;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.ValueIndexing;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;
import de.espirit.firstspirit.client.plugin.report.ReportContext;
import de.espirit.firstspirit.client.plugin.report.ReportItem;
import de.espirit.firstspirit.generate.functions.json.JsonGenerationContext;
import de.espirit.firstspirit.json.JsonElement;
import de.espirit.firstspirit.ui.operations.RequestOperation;
import de.espirit.firstspirit.webedit.plugin.report.WebeditExecutableReportItem;

import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import javax.swing.ImageIcon;

import static java.lang.String.format;

/**
 * Abstract class that provides methods needed in all DataAccessPlugins
 *
 * @param <T> Type of the proxy class
 */
public abstract class EcomAbstract<T extends EcomId> implements DataAccessPlugin<T>, EcomDataAccessPlugin.Defaults<T> {

    protected static final String ERROR_BRIDGE_CONNECTION = "Error while connecting to bridge";
    protected static final String ERROR_LOG_MESSAGE = "%s - %s";
    protected final DataAccessAspectMap dataAccessAspects = new DataAccessAspectMap();
    protected EcomConnectScope scope;

    /**
     * Opens an error dialog with the title "Error while connecting to bridge" in the Content Creator.
     *
     * @param message   The Message to display
     * @param errorCode The error code to display
     */
    public void openDialog(String message, String errorCode) {
        RequestOperation alert = scope.getBroker().requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
        Objects.requireNonNull(alert).setKind(RequestOperation.Kind.ERROR);
        alert.setTitle(ERROR_BRIDGE_CONNECTION);
        alert.perform(format("Errorcode: %s | %s", errorCode, message));
    }

    @Override
    public void setUp(@NotNull BaseContext context) {
        scope = new EcomConnectScope(context);
        final long projectId = context.requireSpecialist(ProjectAgent.TYPE).getId();

        if (ProjectAppHelper.isInstalled(context, projectId) && isAvailable()) {

            dataAccessAspects.put(Reporting.TYPE, active -> {
                if (context.is(BaseContext.Env.WEBEDIT)) {
                    return context.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(getReportSvgIconPath());
                } else {
                    return context.requireSpecialist(ImageAgent.TYPE).getImageFromIcon(getReportPngImageIcon());
                }
            });

            dataAccessAspects.put(ReportItemsProviding.TYPE, new ReportItemsProviding<T>() {

                @Override
                public ReportItem<T> getClickItem() {
                    if (context.is(BaseContext.Env.WEBEDIT)) {
                        return new WebeditExecutableReportItem<>() {

                            @Override
                            public boolean isVisible(@NotNull ReportContext<T> context) {
                                return true;
                            }

                            @Override
                            public boolean isEnabled(@NotNull ReportContext<T> context) {
                                return true;
                            }

                            @Override
                            public @NotNull String getLabel(@NotNull ReportContext<T> context) {
                                return "";
                            }

                            @Override
                            public void execute(@NotNull ReportContext<T> context) {
                                onItemClick(context);
                            }

                            @Override
                            public String getIconPath(@NotNull ReportContext<T> context) {
                                return null;
                            }
                        };
                    }
                    return null;
                }

                @Override
                public @NotNull Collection<? extends ReportItem<T>> getItems() {
                    return getReportItemActions();
                }
            });

            dataAccessAspects.put(StaticItemsProviding.TYPE, () -> {
                Collection<EcomDataAccessPlugin.StaticReportItem> items = getStaticReportItems();
                return items == null ? Collections.emptyList() : items;
            });
        }
    }

    @Override
    public <A> A getAspect(final @NotNull DataAccessAspectType<A> dataAccessAspectType) {
        return dataAccessAspects.get(dataAccessAspectType);
    }

    @Override
    public @NotNull String getLabel() {
        return getReportLabel();
    }

    @Override
    public @NotNull DataAccessSessionBuilder<T> createSessionBuilder() {
        return new DataAccessSessionBuilder<>() {

            private final SessionBuilderAspectMap sessionBuilderAspects = new SessionBuilderAspectMap();

            @Override
            public <A> A getAspect(@NotNull SessionBuilderAspectType<A> aspect) {
                return sessionBuilderAspects.get(aspect);
            }

            @Override
            public @NotNull DataAccessSession<T> createSession(@NotNull BaseContext context) {
                SessionAspectMap sessionAspects = new SessionAspectMap();

                sessionAspects.put(TransferHandling.TYPE, (TransferHandling<T>) host -> {
                    TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
                    host.registerHandler(transferAgent.getRawValueType(getType()), list -> list);
                    host.registerHandler(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, getType()), list -> list);
                });

                sessionAspects.put(TransferSupplying.TYPE, (TransferSupplying<T>) host -> {
                    TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
                    host.registerSupplier(transferAgent.getRawValueType(getType()), Collections::singletonList);
                    host.registerSupplier(transferAgent.getPlainTextType(), item -> Collections.singletonList(getItemId(item)));
                    host.registerSupplier(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, getType()), Collections::singletonList);
                });

                sessionAspects.put(ValueIndexing.TYPE, (identifier, language, recursive, indexer) ->
                    indexer.append(ValueIndexer.VALUE_FIELD, identifier));

                sessionAspects.put(DataTemplating.TYPE, new DataTemplating<T>() {

                    @Override
                    public String getTemplate(@NotNull T item, @NotNull Language language) {
                        return getItemFlyoutHtml(item, language);
                    }

                    @Override
                    public void registerParameters(@NotNull ParameterSet params, @NotNull T item, @NotNull Language language) {
                        // Do nothing because the template doesn't use placeholder
                    }
                });

                sessionAspects.put(JsonSupporting.TYPE, new JsonSupporting<T>() {

                    @Override
                    public @NotNull JsonElement<?> handle(@NotNull JsonGenerationContext context, @NotNull T item) {
                        return Objects.requireNonNull(Json.asFSJsonElement(item.getValue()));
                    }

                    @Override
                    public @NotNull Class<T> getSupportedClass() {
                        return getType();
                    }
                });

                return new DataAccessSession<>() {

                    @Override
                    public <A> A getAspect(@NotNull SessionAspectType<A> aspect) {
                        return sessionAspects.get(aspect);
                    }

                    @Override
                    public @NotNull T getData(@NotNull String identifier) throws NoSuchElementException {
                        List<T> data = getData(Collections.singletonList(identifier));
                        if (data.isEmpty()) {
                            throw new NoSuchElementException("Could not resolve id: " + identifier);
                        }
                        return data.get(0);
                    }

                    @Override
                    public @NotNull List<T> getData(@NotNull Collection<String> identifiers) {
                        return resolve(identifiers);
                    }

                    @Override
                    public @NotNull String getIdentifier(@NotNull T item) throws NoSuchElementException {
                        return getItemId(item);
                    }

                    @Override
                    public @NotNull DataSnippetProvider<T> createDataSnippetProvider() {

                        return new DataSnippetProvider<>() {

                            @Override
                            public Image<?> getIcon(@NotNull T item) {
                                return context.is(BaseContext.Env.WEBEDIT)
                                    ? context.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(getReportSvgIconPath())
                                    : context.requireSpecialist(ImageAgent.TYPE).getImageFromIcon(getReportPngImageIcon());
                            }

                            @Override
                            public Image<?> getThumbnail(@NotNull T item, Language language) {
                                String imageUrl = getItemImageUrl(item, language);
                                return Strings.isEmpty(imageUrl) ? null : context.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(imageUrl);
                            }

                            @Override
                            public @NotNull String getHeader(@NotNull T item, Language language) {
                                return getItemHeader(item, language);
                            }

                            @Override
                            public String getExtract(@NotNull T item, Language language) {
                                return getItemExtract(item, language);
                            }
                        };
                    }

                    @Override
                    public @NotNull DataStreamBuilder<T> createDataStreamBuilder() {
                        StreamBuilderAspectMap streamBuilderAspects = new StreamBuilderAspectMap();

                        EcomFilterBuilder filters = new EcomFilterBuilder();
                        getReportFilter(filters);
                        streamBuilderAspects.put(Filterable.TYPE, filters.asFilterable());

                        return new DataStreamBuilder<>() {

                            @Override
                            public <A> A getAspect(@NotNull StreamBuilderAspectType<A> aspect) {
                                return streamBuilderAspects.get(aspect);
                            }

                            @Override
                            public @NotNull DataStream<T> createDataStream() {
                                Iterator<T> iterator = getItems(filters.getFilter());
                                final int[] total = {0};

                                return new DataStream<>() {

                                    @Override
                                    public @NotNull List<T> getNext(int count) {
                                        count = Math.max(count, 30);

                                        Set<T> items = new LinkedHashSet<>();
                                        while (--count > 0 && iterator.hasNext()) {
                                            items.add(iterator.next());
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
                                };
                            }
                        };
                    }
                };
            }
        };
    }

    @Override
    public Image<?> getIcon() {
        return null;
    }

    @Override
    public void tearDown() {
        // Not needed.
    }

    public abstract Class<T> getType();

    public abstract String getReportSvgIconPath();

    public abstract ImageIcon getReportPngImageIcon();

    public abstract String getReportLabel();

    public abstract List<T> resolve(Collection<String> identifiers);

    public abstract Iterator<T> getItems(Map<String, String> filters);

    public abstract String getItemId(T item);

    public abstract String getItemHeader(T item, Language language);
}
