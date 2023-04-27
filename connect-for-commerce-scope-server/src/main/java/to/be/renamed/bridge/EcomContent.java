package to.be.renamed.bridge;

import to.be.renamed.bridge.client.Json;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.EcomConnectException;
import to.be.renamed.EcomConnectScope;
import de.espirit.firstspirit.access.store.sitestore.PageRef;

import java.io.Serializable;

import static java.lang.String.format;

public class EcomContent extends EcomId implements Serializable {

    private static final long serialVersionUID = -7268004405427482123L;
    private final String extract;

    public EcomContent(Json json) {
        super(json);
        extract = json .get("extract");
    }

    @Override
    public String getType() {
        return EcomId.CONTENT_TEMPLATE_UID;
    }

    public String getExtract() {
        return extract;
    }

    public void ensureExistence(EcomConnectScope scope) throws EcomConnectException {
        if (!hasId()) {
            EcomElement element = getElement(scope);
            PageRef pageRef = element.getPageRef();
            if (pageRef.getPage() != null && getPageId(pageRef.getPage(), element.getLanguage()) == null) {
                String pageId;
                if (ServiceFactory.getBridgeService(scope.getBroker()).hasNewContentEndpoint()) {
                    pageId = ServiceFactory.getBridgeService(scope.getBroker()).createContent(element.getEcomElementDTO());
                } else {
                    pageId = ServiceFactory.getBridgeService(scope.getBroker()).createContentPage(element.getEcomElementDTO(), getLang());
                }
                if (pageId == null) {
                    if (ServiceFactory.getBridgeService(scope.getBroker()).hasNewContentEndpoint()) {
                        throw new EcomConnectException(
                                format(
                                        "problem creating page%n\tlang: %s%n\tjson: %s", getLang(), element.getEcomElementDTO().getJsonModel().json()));
                    } else {
                        throw new EcomConnectException(
                                format(
                                        "problem creating page%n\tlang: %s%n\tjson: %s", getLang(), element.getEcomElementDTO().getOldJsonModel().json()));
                    }
                }
                element.updatePageId(pageId);
                setId(pageId);
            }
        }
    }
}
