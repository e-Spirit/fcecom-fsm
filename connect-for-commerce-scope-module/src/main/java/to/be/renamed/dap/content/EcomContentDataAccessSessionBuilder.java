package to.be.renamed.dap.content;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomContent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;

import org.jetbrains.annotations.NotNull;

public class EcomContentDataAccessSessionBuilder implements DataAccessSessionBuilder<EcomContent> {

    private static final Class<?> LOGGER = EcomContentDataAccessSessionBuilder.class;
    private final SessionBuilderAspectMap sessionBuilderAspects = new SessionBuilderAspectMap();
    private final EcomConnectScope scope;

    public EcomContentDataAccessSessionBuilder(final EcomConnectScope scope) {
        this.scope = scope;
    }

    @Override
    public <A> A getAspect(@NotNull SessionBuilderAspectType<A> aspect) {
        return sessionBuilderAspects.get(aspect);
    }

    @Override
    public @NotNull DataAccessSession<EcomContent> createSession(final @NotNull BaseContext context) {
        Logging.logDebug("Create new DataAccessSession", LOGGER);
        return new EcomContentDataAccessSession(context, scope);
    }
}
