package to.be.renamed.dap.product;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomProduct;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSession;
import de.espirit.firstspirit.client.plugin.dataaccess.DataAccessSessionBuilder;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectMap;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.SessionBuilderAspectType;

import org.jetbrains.annotations.NotNull;

public class EcomProductDataAccessSessionBuilder implements DataAccessSessionBuilder<EcomProduct> {

    private static final Class<?> LOGGER = EcomProductDataAccessSessionBuilder.class;
    private final SessionBuilderAspectMap sessionBuilderAspects = new SessionBuilderAspectMap();
    private final EcomConnectScope scope;

    public EcomProductDataAccessSessionBuilder(final EcomConnectScope scope) {
        this.scope = scope;
    }

    @Override
    public <A> A getAspect(@NotNull SessionBuilderAspectType<A> aspect) {
        return sessionBuilderAspects.get(aspect);
    }

    @Override
    public @NotNull DataAccessSession<EcomProduct> createSession(final @NotNull BaseContext context) {
        Logging.logDebug("Create new DataAccessSession", LOGGER);
        return new EcomProductDataAccessSession(context, scope);
    }
}
