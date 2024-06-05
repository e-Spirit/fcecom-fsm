package to.be.renamed.dap.product.aspects;

import to.be.renamed.bridge.EcomProduct;
import to.be.renamed.bridge.client.Json;

import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.generate.functions.json.JsonGenerationContext;
import de.espirit.firstspirit.json.JsonElement;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EcomProductJsonSupporting implements JsonSupporting<EcomProduct> {

    @Override
    public @NotNull JsonElement<?> handle(@NotNull JsonGenerationContext context, @NotNull EcomProduct item) {
        return Objects.requireNonNull(Json.asFSJsonElement(item.getValue()));
    }

    @Override
    public @NotNull Class<EcomProduct> getSupportedClass() {
        return EcomProduct.class;
    }
}
