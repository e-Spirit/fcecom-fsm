package to.be.renamed.dap.content.aspects;

import to.be.renamed.bridge.EcomContent;

import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;

import org.jetbrains.annotations.NotNull;

public class EcomContentDataTemplating implements DataTemplating<EcomContent> {

    @Override
    public String getTemplate(@NotNull final EcomContent item, @NotNull final Language language) {
        return null;
    }

    @Override
    public void registerParameters(@NotNull final ParameterSet params, @NotNull final EcomContent item,
                                   @NotNull final Language language) {
        // Do nothing because the template doesn't use placeholder
    }
}
