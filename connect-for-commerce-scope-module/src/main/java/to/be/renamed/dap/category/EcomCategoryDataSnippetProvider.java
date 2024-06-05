package to.be.renamed.dap.category;

import to.be.renamed.bridge.EcomCategory;
import to.be.renamed.module.EcomConnectWebApp;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;

import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;

public class EcomCategoryDataSnippetProvider implements DataSnippetProvider<EcomCategory> {

    private final BaseContext baseContext;

    public EcomCategoryDataSnippetProvider(final BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    @Override
    public Image<?> getIcon(@NotNull EcomCategory item) {
        return baseContext.is(BaseContext.Env.WEBEDIT)
               ? baseContext.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(EcomConnectWebApp.CATEGORY_DAP_ICON)
               : baseContext.requireSpecialist(ImageAgent.TYPE).getImageFromIcon(new ImageIcon(getClass().getResource("/files-web/fcecom-category.png")));
    }

    @Override
    public Image<?> getThumbnail(@NotNull EcomCategory item, Language language) {
        return null;
    }

    @Override
    public @NotNull String getHeader(@NotNull EcomCategory item, Language language) {
        return item.getLabel();
    }

    @Override
    public String getExtract(@NotNull EcomCategory item, Language language) {
        return null;
    }
}
