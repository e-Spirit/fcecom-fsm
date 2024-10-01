package to.be.renamed.bridge;

import java.io.Serial;
import java.io.Serializable;

public class TestConnectionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2912834619367127011L;

    private final String httpMethod;
    private final String url;
    private final boolean deprecated;

    private TestConnectionRequest(String httpMethod, String url, boolean deprecated) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.deprecated = deprecated;
    }

    public static TestConnectionRequest withParams(String httpMethod, String url) {
        return new TestConnectionRequest(httpMethod, url, false);
    }

    public static TestConnectionRequest withParams(String httpMethod, String url, boolean deprecated) {
        return new TestConnectionRequest(httpMethod, url, deprecated);
    }

    public String httpMethod() {
        return httpMethod;
    }

    public String url() {
        return url;
    }

    public boolean deprecated() {
        return deprecated;
    }
}
