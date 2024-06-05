package to.be.renamed.dap.category.aspects;

import to.be.renamed.bridge.EcomCategory;

import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.agency.TransferAgent;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.SupplierHost;
import de.espirit.firstspirit.client.plugin.dataaccess.aspects.transfer.TransferSupplying;

import java.awt.datatransfer.DataFlavor;
import java.util.Collections;

public class EcomCategoryTransferSupplying implements TransferSupplying<EcomCategory> {

    private final BaseContext context;

    public EcomCategoryTransferSupplying(final BaseContext context) {
        this.context = context;
    }

    @Override
    public void registerSuppliers(final SupplierHost<EcomCategory> host) {
        TransferAgent transferAgent = context.requireSpecialist(TransferAgent.TYPE);
        host.registerSupplier(transferAgent.getRawValueType(EcomCategory.class), Collections::singletonList);
        host.registerSupplier(transferAgent.getPlainTextType(), item -> Collections.singletonList(item.getId()));
        host.registerSupplier(transferAgent.getType(DataFlavor.javaSerializedObjectMimeType, EcomCategory.class), Collections::singletonList);
    }
}
