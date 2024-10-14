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
        extract = json.get("extract");
    }

    public EcomContent(String id, String type, String lang, String pageRefUid, String label, String extract) {
        super(id, type, lang, pageRefUid, label);
        this.extract = extract;
    }

    @Override
    public String getType() {
        return EcomId.CONTENT_TEMPLATE_UID;
    }

    public String getExtract() {
        return extract;
    }

    @Override
    public String toString() {
        return format("EcomContent: {id: %s, type: %s, lang: %s, pageRefUid: %s, label: %s, extract: %s}", id, type, lang, pageRefUid, label,
                      extract);
    }

    public void ensureExistence(EcomConnectScope scope) throws EcomConnectException {
        if (!hasId()) {
            EcomElement element = getElement(scope);
            PageRef pageRef = element.getPageRef();
            if (pageRef.getPage() != null && getPageId(pageRef.getPage(), element.getLanguage()) == null) {
                String pageId;
                pageId = ServiceFactory.getBridgeService(scope.getBroker()).createContent(element.getEcomElementDTO());

                if (pageId == null) {
                    throw new EcomConnectException(
                        format(
                            "problem creating page%n\tlang: %s%n\tjson: %s", getLang(), element.getEcomElementDTO().getJsonModel().toString()));

                }
                element.updatePageId(pageId);
                setId(pageId);
            }
        }
    }
}
