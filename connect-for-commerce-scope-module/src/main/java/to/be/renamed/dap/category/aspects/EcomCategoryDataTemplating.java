package to.be.renamed.dap.category.aspects;

import to.be.renamed.bridge.EcomCategory;

import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;

import org.jetbrains.annotations.NotNull;

public class EcomCategoryDataTemplating implements DataTemplating<EcomCategory> {

    @Override
    public String getTemplate(@NotNull final EcomCategory item, @NotNull final Language language) {
        return null;
    }

    @Override
    public void registerParameters(@NotNull final ParameterSet params, @NotNull final EcomCategory item,
                                   @NotNull final Language language) {
        // Do nothing because the template doesn't use placeholder
    }
}
