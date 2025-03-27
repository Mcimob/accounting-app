package ch.pfaditools.accounting.backend.service.impl;

import ch.pfaditools.accounting.backend.dao.PaymentDao;
import ch.pfaditools.accounting.backend.service.PaymentService;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl extends BaseServiceImpl<PaymentEntity, PaymentEntityFilter> implements PaymentService {
    public PaymentServiceImpl(PaymentDao dao) {
        super(dao);
    }
}
