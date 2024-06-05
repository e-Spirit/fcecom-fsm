package to.be.renamed.dap.content.aspects;

import to.be.renamed.bridge.EcomContent;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.SupplierHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;

import java.awt.datatransfer.DataFlavor;
import java.util.Collections;

public class EcomContentTransferSupplying implements TransferSupplying<EcomContent> {

    private final BaseContext context;

    public EcomContentTransferSupplying(final BaseContext context) {
        this.context = context;
    }

    @Override
    public void registerSuppliers(final SupplierHost<EcomContent> host) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        host.registerSupplier(transferAgent.getRawValueType(EcomContent.class), Collections::singletonList);
        host.registerSupplier(transferAgent.getPlainTextType(), item -> Collections.singletonList(item.getId()));
        host.registerSupplier(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, EcomContent.class), Collections::singletonList);
    }
}
