package to.be.renamed.dap.content.aspects;

import to.be.renamed.bridge.EcomContent;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.HandlerHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferHandling;

import java.awt.datatransfer.DataFlavor;

public class EcomContentTransferHandling implements TransferHandling<EcomContent> {

    private final BaseContext context;

    public EcomContentTransferHandling(final BaseContext context) {
        this.context = context;
    }

    @Override
    public void registerHandlers(final HandlerHost<EcomContent> handlerHost) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        handlerHost.registerHandler(transferAgent.getRawValueType(EcomContent.class), list -> list);
        handlerHost.registerHandler(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, EcomContent.class), list -> list);
    }
}
