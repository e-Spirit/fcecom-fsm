package to.be.renamed.dap.category;

import to.be.renamed.bridge.EcomCategory;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.swing.ImageIcon;

public class EcomCategoryDataSnippetProvider implements DataSnippetProvider<EcomCategory> {

    private final BaseContext baseContext;

    public EcomCategoryDataSnippetProvider(final BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    private Image<?> getImage(EcomCategoryReportIcon icon) {
        return baseContext.is(BaseContext.Env.WEBEDIT)
               ? baseContext.requireSpecialist(ImageAgent.TYPE)
                   .getImageFromUrl(icon.webEditFile())
               : baseContext.requireSpecialist(ImageAgent.TYPE)
                   .getImageFromIcon(new ImageIcon(Objects.requireNonNull(
                       getClass().getResource(icon.siteArchitectFile()))));
    }

    @Override
    public Image<?> getIcon(@NotNull EcomCategory item) {
        return getImage(EcomCategoryReportIcon.managed(item.isManaged()));
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
