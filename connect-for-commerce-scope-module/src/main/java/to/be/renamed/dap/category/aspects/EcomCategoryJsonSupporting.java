package to.be.renamed.dap.category.aspects;

import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.bridge.client.Json;

import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.generate.functions.json.JsonGenerationContext;
import de.espirit.firstspirit.json.JsonElement;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EcomCategoryJsonSupporting implements JsonSupporting<EcomCategory> {

    @Override
    public @NotNull JsonElement<?> handle(@NotNull JsonGenerationContext context, @NotNull EcomCategory item) {
        return Objects.requireNonNull(Json.asFSJsonElement(item.getValue()));
    }

    @Override
    public @NotNull Class<EcomCategory> getSupportedClass() {
        return EcomCategory.class;
    }
}
