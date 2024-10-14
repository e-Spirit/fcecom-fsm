package to.be.renamed.dap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.EcomId;
import to.be.renamed.bridge.EcomSearchResult;

import de.espirit.common.TypedFilter;
import de.espirit.firstspirit.access.store.Store;
import de.espirit.firstspirit.access.store.pagestore.Page;
import de.espirit.firstspirit.agency.OperationAgent;
import de.espirit.firstspirit.agency.StoreAgent;
import de.espirit.firstspirit.ui.operations.RequestOperation;

import java.io.Serial;
import java.util.Objects;

import static java.lang.String.format;

public class EcomDapUtilities {

    public static final String ERROR_BRIDGE_CONNECTION = "Error while connecting to bridge";
    public static final String ERROR_LOG_MESSAGE = "%s - %s";
    public static final String FILTER_QUERY = "q";
    public static final String FILTER_CATEGORY = "categoryId";
    public static final String FILTER_PARENT_ID = "parentId";

    private EcomDapUtilities() {
    }

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

    /**
     * Fetches FirstSpirit pages and searches for shop driven pages
     * managed by FirstSpirit, then sets the {isManaged} flag.
     *
     * @param items Processed Bridge response items.
     * @param scope EcomConnectScope needed for FS page fetch.
     * @param <T>   Any subtype of EcomId
     * @return Altered provided items list with managed flags.
     */
    public static <T extends EcomId> EcomSearchResult<T> applyManagedFlag(EcomSearchResult<T> items, EcomConnectScope scope) {
        // Bridge Results as map with the ID as Key
        final ImmutableMap<String, T> bridgeItems = Maps.uniqueIndex(items.getResults(), EcomId::getId);

        TypedFilter<Page> pageFilter = new TypedFilter<>(Page.class) {
            @Serial
            private static final long serialVersionUID = 278619144342422965L;

            @Override
            public boolean accept(Page page) {
                if (!EcomId.hasPageIdField(page)) {
                    return false;
                }

                return bridgeItems.containsKey(EcomId.getPageId(page, null));
            }
        };

        // Has FirstSpirit page &
        scope.getBroker().requireSpecialist(StoreAgent.TYPE).getStore(Store.Type.PAGESTORE)

            // Get all FirstSpirit Pages
            .getChildren(pageFilter, true).toStream()

            // Only pages with existing PageReferences
            .filter(fsPage -> !fsPage.getPageRefs().isEmpty())

            // Get item from Bridge Results
            .forEach(fsPage -> Objects.requireNonNull(bridgeItems.get(EcomId.getPageId(fsPage, null)))

                // Set managed flag
                .setManaged(true));

        return items;
    }
}
