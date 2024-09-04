package to.be.renamed.fspage;

import to.be.renamed.bridge.EcomProduct;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.access.store.ElementDeletedException;
import de.espirit.firstspirit.access.store.LockException;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.ai.GenerativeAIAgent;
import de.espirit.firstspirit.forms.FormField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

import static to.be.renamed.fspage.ai.prompt.Prompt.PDP_KEYWORD_GENERATION;
import static java.lang.String.format;

public class FsPageEnhancer {

    private GenerativeAIAgent aiAgent;

    public FsPageEnhancer(final BaseContext context) {
        aiAgent = context.requireSpecialist(GenerativeAIAgent.TYPE);
    }

    public void setPdpMetaKeywords(@NotNull final Page page, @NotNull final Language language, @NotNull final String fieldName,
                                   @NotNull final EcomProduct product) {
        final String keywords = generatePdpKeywords(product.getLabel(), product.getExtract());

        if (keywords != null) {
            Logging.logDebug(format("Trying to set meta keywords for page with id '%s'.", page.getId()), getClass());
            boolean result = setInputText(page, language, fieldName, keywords);
            if (result) {
                Logging.logDebug(format("Successfully set meta keywords for page with id '%s'.", page.getId()), getClass());
            } else {
                Logging.logError(format("Could not set keywords for page with id '%s'.", page.getId()), getClass());
            }
        } else {
            Logging.logError(format("Could not set keywords for page with id '%s' because keywords were null.", page.getId()), getClass());
        }
    }

    private boolean setInputText(@NotNull final Page page, @NotNull final Language language, @NotNull final String fieldName,
                                 @NotNull final String content) {
        try {
            if (!page.isLocked()) {
                page.setLock(true);
            }

            FormField<?> formField = page.getFormData().get(language, fieldName);
            formField.set(content);

            if (page.isLocked()) {
                page.setLock(false);
            }
            return true;

        } catch (LockException lockException) {
            Logging.logError(format("Unable to lock page '%s'.", page.getId()), lockException, getClass());
        } catch (ElementDeletedException elementDeletedException) {
            Logging.logError(format("Page '%s' is not available", page.getId()), elementDeletedException, getClass());
        } finally {
            try {
                page.setLock(false);
            } catch (LockException | ElementDeletedException e) {
                Logging.logError("Could not remove lock from page %s." + page.getUid(), e, getClass());
            }
        }
        return false;
    }

    @Nullable
    private String generatePdpKeywords(String productName, String productDescription) {
        String keywords = null;
        if (aiAgent != null) {
            try {
                final Optional<String> result = aiAgent.request(format(PDP_KEYWORD_GENERATION, productName, productDescription));
                keywords = result.orElse(null);
            } catch (IOException e) {
                Logging.logError(e.getMessage(), e, getClass());
            }
        }
        return keywords;
    }
}
