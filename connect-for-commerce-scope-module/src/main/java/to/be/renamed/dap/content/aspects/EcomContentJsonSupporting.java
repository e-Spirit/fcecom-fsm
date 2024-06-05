package to.be.renamed.dap.content.aspects;

import to.be.renamed.bridge.EcomContent;
import to.be.renamed.bridge.client.Json;

import de.espirit.firstspirit.client.plugin.dataaccess.aspects.JsonSupporting;
import de.espirit.firstspirit.generate.functions.json.JsonGenerationContext;
import de.espirit.firstspirit.json.JsonElement;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EcomContentJsonSupporting implements JsonSupporting<EcomContent> {

    @Override
    public @NotNull JsonElement<?> handle(@NotNull JsonGenerationContext context, @NotNull EcomContent item) {
        return Objects.requireNonNull(Json.asFSJsonElement(item.getValue()));
    }

    @Override
    public @NotNull Class<EcomContent> getSupportedClass() {
        return EcomContent.class;
    }
}
