package to.be.renamed.dap.product;

import to.be.renamed.bridge.EcomProduct;
import to.be.renamed.module.EcomConnectWebApp;

import de.espirit.common.tools.Strings;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.Language;
import de.espirit.firstspirit.agency.Image;
import de.espirit.firstspirit.agency.ImageAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.DataSnippetProvider;

import org.jetbrains.annotations.NotNull;

import javax.swing.ImageIcon;

public class EcomProductDataSnippetProvider implements DataSnippetProvider<EcomProduct> {

    private final BaseContext baseContext;

    public EcomProductDataSnippetProvider(final BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    @Override
    public Image<?> getIcon(@NotNull EcomProduct item) {
        return baseContext.is(BaseContext.Env.WEBEDIT)
               ? baseContext.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(EcomConnectWebApp.PRODUCT_DAP_ICON)
               : baseContext.requireSpecialist(ImageAgent.TYPE).getImageFromIcon(new ImageIcon(getClass().getResource("/files-web/fcecom-product.png")));
    }

    @Override
    public Image<?> getThumbnail(@NotNull EcomProduct item, Language language) {
        String imageUrl = item.getThumbnail();
        return Strings.isEmpty(imageUrl) ? null : baseContext.requireSpecialist(ImageAgent.TYPE).getImageFromUrl(imageUrl);
    }

    @Override
    public @NotNull String getHeader(@NotNull EcomProduct item, Language language) {
        return item.getLabel();
    }

    @Override
    public String getExtract(@NotNull EcomProduct item, Language language) {
        return item.getExtract();
    }
}
