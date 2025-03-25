package ch.pfaditools.accounting.backend.dao.impl;

import ch.pfaditools.accounting.backend.dao.PaymentDao;
import ch.pfaditools.accounting.backend.repository.PaymentRepository;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;
import org.springframework.stereotype.Component;

@Component
public class PaymentDaoImpl extends BaseDaoImpl<PaymentEntity, PaymentEntityFilter> implements PaymentDao {
    public PaymentDaoImpl(PaymentRepository repository) {
        super(repository);
    }
}
