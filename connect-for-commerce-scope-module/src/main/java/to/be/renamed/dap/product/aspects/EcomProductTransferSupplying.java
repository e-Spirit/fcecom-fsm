package to.be.renamed.dap.product.aspects;

import to.be.renamed.bridge.EcomProduct;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.SupplierHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;

import java.awt.datatransfer.DataFlavor;
import java.util.Collections;

public class EcomProductTransferSupplying implements TransferSupplying<EcomProduct> {

    private final BaseContext context;

    public EcomProductTransferSupplying(final BaseContext context) {
        this.context = context;
    }

    @Override
    public void registerSuppliers(final SupplierHost<EcomProduct> host) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        host.registerSupplier(transferAgent.getRawValueType(EcomProduct.class), Collections::singletonList);
        host.registerSupplier(transferAgent.getPlainTextType(), item -> Collections.singletonList(item.getId()));
        host.registerSupplier(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, EcomProduct.class), Collections::singletonList);
    }
}
