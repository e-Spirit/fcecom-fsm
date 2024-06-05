package to.be.renamed.dap.product.aspects;

import to.be.renamed.bridge.EcomProduct;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.DataTemplating;

import org.jetbrains.annotations.NotNull;

import static java.lang.String.format;

public class EcomProductDataTemplating implements DataTemplating<EcomProduct> {

    @Override
    public String getTemplate(@NotNull final EcomProduct item, @NotNull final Language language) {
        String image = item.getImage();
        return Strings.notEmpty(image) ? format("<img src=\"%s\" style=\"width: 30vw;\" />", image) : null;
    }

    @Override
    public void registerParameters(@NotNull final DataTemplating.ParameterSet params, @NotNull final EcomProduct item,
                                   @NotNull final Language language) {
        // Do nothing because the template doesn't use placeholder
    }
}
