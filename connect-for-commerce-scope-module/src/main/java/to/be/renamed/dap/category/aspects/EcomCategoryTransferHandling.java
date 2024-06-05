package to.be.renamed.dap.category.aspects;

import to.be.renamed.bridge.EcomCategory;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.HandlerHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;

import java.awt.datatransfer.DataFlavor;

public class EcomCategoryTransferHandling implements TransferHandling<EcomCategory> {

    private final BaseContext context;

    public EcomCategoryTransferHandling(final BaseContext context) {
        this.context = context;
    }

    @Override
    public void registerHandlers(final HandlerHost<EcomCategory> handlerHost) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        handlerHost.registerHandler(transferAgent.getRawValueType(EcomCategory.class), list -> list);
        handlerHost.registerHandler(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, EcomCategory.class), list -> list);
    }
}
