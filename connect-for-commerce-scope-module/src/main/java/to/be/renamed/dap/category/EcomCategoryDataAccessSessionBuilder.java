package to.be.renamed.dap.category;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomCategory;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;

import org.jetbrains.annotations.NotNull;

public class EcomCategoryDataAccessSessionBuilder implements DataAccessSessionBuilder<EcomCategory> {

    private static final Class<?> LOGGER = EcomCategoryDataAccessSessionBuilder.class;
    private final SessionBuilderAspectMap sessionBuilderAspects = new SessionBuilderAspectMap();
    private final EcomConnectScope scope;

    public EcomCategoryDataAccessSessionBuilder(final EcomConnectScope scope) {
        this.scope = scope;
    }

    @Override
    public <A> A getAspect(@NotNull SessionBuilderAspectType<A> aspect) {
        return sessionBuilderAspects.get(aspect);
    }

    @Override
    public @NotNull DataAccessSession<EcomCategory> createSession(final @NotNull BaseContext context) {
        Logging.logDebug("Create new DataAccessSession", LOGGER);
        return new EcomCategoryDataAccessSession(context, scope);
    }
}
