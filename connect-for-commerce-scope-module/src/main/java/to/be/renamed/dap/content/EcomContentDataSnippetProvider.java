package to.be.renamed.dap.content;

import to.be.renamed.bridge.EcomContent;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javax.swing.ImageIcon;

public class EcomContentDataSnippetProvider implements DataSnippetProvider<EcomContent> {

    private final BaseContext baseContext;

    public EcomContentDataSnippetProvider(final BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    private Image<?> getImage(EcomContentReportIcon icon) {
        return baseContext.is(BaseContext.Env.WEBEDIT)
               ? baseContext.requireSpecialist(ImageAgent.TYPE)
                   .getImageFromUrl(icon.webEditFile())
               : baseContext.requireSpecialist(ImageAgent.TYPE)
                   .getImageFromIcon(new ImageIcon(Objects.requireNonNull(
                       getClass().getResource(icon.siteArchitectFile()))));
    }

    @Override
    public Image<?> getIcon(@NotNull EcomContent item) {
        return getImage(EcomContentReportIcon.managed(item.isManaged()));
    }

    @Override
    public Image<?> getThumbnail(@NotNull EcomContent item, Language language) {
        return null;
    }

    @Override
    public @NotNull String getHeader(@NotNull EcomContent item, Language language) {
        return item.getLabel();
    }

    @Override
    public String getExtract(@NotNull EcomContent item, Language language) {
        return item.getExtract();
    }
}
