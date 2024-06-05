package to.be.renamed.dap;

import to.be.renamed.EcomConnectScope;

import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.ui.operations.RequestOperation;

import java.util.Objects;

import static java.lang.String.format;

public class EcomDapUtilities {

    public static final String ERROR_BRIDGE_CONNECTION = "Error while connecting to bridge";
    public static final String ERROR_LOG_MESSAGE = "%s - %s";
    public static final String FILTER_QUERY = "q";
    public static final String FILTER_CATEGORY = "categoryId";
    public static final String FILTER_PARENT_ID = "parentId";

    private EcomDapUtilities() {}

    /**
     * Opens an error dialog with the title "Error while connecting to bridge" in the Content Creator.
     *
     * @param message   The Message to display
     * @param errorCode The error code to display
     * @param scope     The EcomConnectScope
     */
    public static void openDialog(String message, String errorCode, EcomConnectScope scope) {
        RequestOperation alert = scope.getBroker().requireSpecialist(OperationAgent.TYPE).getOperation(RequestOperation.TYPE);
        Objects.requireNonNull(alert).setKind(RequestOperation.Kind.ERROR);
        alert.setTitle(ERROR_BRIDGE_CONNECTION);
        alert.perform(format("Errorcode: %s | %s", errorCode, message));
    }
}
