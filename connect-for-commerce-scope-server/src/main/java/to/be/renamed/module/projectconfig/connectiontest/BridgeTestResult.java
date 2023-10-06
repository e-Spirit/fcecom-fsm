package to.be.renamed.module.projectconfig.connectiontest;

import java.io.Serializable;
import java.util.Objects;

import static to.be.renamed.module.projectconfig.connectiontest.EcomTaskResult.DISABLED;
import static to.be.renamed.module.projectconfig.connectiontest.EcomTaskResult.FAILED;
import static to.be.renamed.module.projectconfig.connectiontest.EcomTaskResult.PROBLEMATIC;
import static to.be.renamed.module.projectconfig.connectiontest.EcomTaskResult.SUCCESSFUL;
import static to.be.renamed.module.projectconfig.connectiontest.EcomTaskResult.UNKNOWN;
import static de.espirit.common.base.Logging.logInfo;

/**
 * A class for displaying the result of a bridge test.
 * It holds all necessary information to display a
 *  successful run, but also any problem occurred in a request.
 * With this class it is possible to transfer the necessary information
 *  between the Server plugin and the ServerManager interface.
 */
public class BridgeTestResult implements Serializable {
    private static final long serialVersionUID = -8311754019008068549L;

    /**
     * A flag to tell the user that - despite successful or not -
     * the requested endpoint will be removed within the deprecation period.
     */
    private boolean deprecated;

    // Result
    private EcomTaskResult taskResult = UNKNOWN;

    // Errors
    private String parsingError;
    private String errorResponse;

    // Request
    private String httpMethod;
    private String url;

    // Response
    private int status;
    private String statusText;

    // Exception
    private String exceptionMessage;

    public boolean isDeprecated() {
        return deprecated;
    }

    public EcomTaskResult getTaskResult() {
        return taskResult;
    }

    public String getParsingError() {
        return parsingError;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public int getStatus() {
        return status;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public BridgeTestResult setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
        return this;
    }

    public void setTaskResult(EcomTaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public BridgeTestResult setException(Exception exception) {
        if (exception != null) {
            this.exceptionMessage = exception.getMessage();
            setTaskResult(FAILED);
        } else {
            this.exceptionMessage = null;
        }
        return this;
    }

    public void setParsingError(String parsingError) {
        if (parsingError == null) {
            return;
        }

        this.parsingError = parsingError;
        setTaskResult(PROBLEMATIC);
    }

    public void setErrorResponse(String errorResponse) {
        if (errorResponse == null) {
            return;
        }

        this.errorResponse = errorResponse;
        setTaskResult(PROBLEMATIC);
    }

    public BridgeTestResult setRequestData(String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;

        return this;
    }

    public BridgeTestResult setResponseData(int status, String statusText) {
        this.status = status;
        this.statusText = statusText;

        if (status >= 100 && status < 300) {
            setTaskResult(SUCCESSFUL);
        } else if (status == 404 && Objects.equals(this.httpMethod, "HEAD")) {
            setTaskResult(DISABLED);
        } else {
            setTaskResult(PROBLEMATIC);
        }

        return this;
    }

    public String summarizeRequest() {
        StringBuilder msg = new StringBuilder();

        if (httpMethod != null) {
            msg.append(httpMethod).append(" ");
        }
        if (url != null) {
            msg.append(url);
        }

        logInfo(statusText, getClass());

        if (status > 0) {
            msg.append(" · ").append(status);
        }
        if (statusText != null) {
            msg.append(" · ").append(statusText);
        }

        return msg.toString();
    }
}