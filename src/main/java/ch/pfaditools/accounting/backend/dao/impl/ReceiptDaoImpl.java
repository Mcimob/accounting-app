package ch.pfaditools.accounting.backend.dao.impl;

import ch.pfaditools.accounting.backend.dao.ReceiptDao;
import ch.pfaditools.accounting.backend.repository.ReceiptRepository;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import org.springframework.stereotype.Component;

@Component
public class ReceiptDaoImpl extends BaseDaoImpl<ReceiptEntity, ReceiptEntityFilter> implements ReceiptDao {

    public ReceiptDaoImpl(ReceiptRepository repository) {
        super(repository);
    }
}
