package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.PaymentService;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;

public class PaymentProvider extends AbstractEntityProvider<PaymentEntity, PaymentEntityFilter> {
    public PaymentProvider(PaymentService service) {
        super(service);
    }

    @Override
    protected PaymentEntityFilter getFilter() {
        return new PaymentEntityFilter();
    }
}
