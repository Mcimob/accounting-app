package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;

public class ReceiptProvider extends AbstractEntityProvider<ReceiptEntity, ReceiptEntityFilter> {

    public ReceiptProvider(ReceiptService service) {
        super(service);
    }

    @Override
    protected ReceiptEntityFilter getFilter() {
        return new ReceiptEntityFilter();
    }
}
