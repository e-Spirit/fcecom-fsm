package to.be.renamed.dap.product.aspects;

import to.be.renamed.bridge.EcomProduct;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.HandlerHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author mikula
 */
public class EcomProductTransferHandling implements TransferHandling<EcomProduct> {

    private final BaseContext context;

    public EcomProductTransferHandling(final BaseContext context) {
        this.context = context;
    }

    @Override
    public void registerHandlers(final HandlerHost<EcomProduct> handlerHost) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        handlerHost.registerHandler(transferAgent.getRawValueType(EcomProduct.class), list -> list);
        handlerHost.registerHandler(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, EcomProduct.class), list -> list);
    }
}
