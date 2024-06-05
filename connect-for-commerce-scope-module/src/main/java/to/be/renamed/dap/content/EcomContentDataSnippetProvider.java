package to.be.renamed.dap.content;

import to.be.renamed.bridge.EcomContent;
import to.be.renamed.module.EcomConnectWebApp;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;

import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;

public class EcomContentDataSnippetProvider implements DataSnippetProvider<EcomContent> {

    private final BaseContext baseContext;

    public EcomContentDataSnippetProvider(final BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    @Override
    public Image<?> getIcon(@NotNull EcomContent item) {
        return baseContext.is(BaseContext.Env.WEBEDIT)
               ? baseContext.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(EcomConnectWebApp.CONTENT_DAP_ICON)
               : baseContext.requireSpecialist(ImageAgent.TYPE).getImageFromIcon(new ImageIcon(getClass().getResource("/files-web/fcecom-content.png")));
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
