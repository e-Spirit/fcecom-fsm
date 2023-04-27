package to.be.renamed.module;

import com.espirit.moddev.components.annotations.WebAppComponent;
import com.espirit.moddev.components.annotations.WebResource;

import de.espirit.firstspirit.module.AbstractWebApp;
import de.espirit.firstspirit.module.WebApp;

@WebAppComponent(
    name = EcomConnectWebApp.WEB_APP_NAME,
    displayName = EcomConnectWebApp.WEB_APP_NAME,
    webXml = "files-web/web.xml",
    webResources = {
        @WebResource(name = "com.espirit.firstspirit.ecom.connect.fcecom.web", version = "1", path = "files-web/", targetPath = "/"),
    }
)
public class EcomConnectWebApp extends AbstractWebApp implements WebApp {

    public static final String WEB_APP_NAME = ProjectAppHelper.MODULE_NAME + " - Web Application Component";

    public static final String CATEGORY_DAP_ICON = "fcecom-category.svg";
    public static final String CONTENT_DAP_ICON = "fcecom-content.svg";
    public static final String PRODUCT_DAP_ICON = "fcecom-product.svg";

    @Override
    public void installed() {
        // Not used
    }

    @Override
    public void updated(String oldVersionString) {
        // Not used
    }
}
