package to.be.renamed.dap.content;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomContent;
import to.be.renamed.dap.EcomDapUtilities;
import to.be.renamed.dap.EcomFilterBuilder;

import de.espirit.firstspirit.client.plugin.dataaccess.DataStream;
import de.espirit.firstspirit.client.plugin.dataaccess.DataStreamBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.Filterable;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.StreamBuilderAspectType;

import org.jetbrains.annotations.NotNull;

public class EcomContentDataStreamBuilder implements DataStreamBuilder<EcomContent> {

    private final StreamBuilderAspectMap streamBuilderAspects;
    private final EcomConnectScope scope;
    private final EcomFilterBuilder filterBuilder;

    public EcomContentDataStreamBuilder(final EcomConnectScope scope) {
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
    public @NotNull DataStream<EcomContent> createDataStream() {
        return new EcomContentDataStream(scope, filterBuilder);
    }

    private void getReportFilter(EcomFilterBuilder filterBuilder) {
        filterBuilder.addTextField(EcomDapUtilities.FILTER_QUERY, scope.getLabel("report.contents.filter.q"));
    }
}
