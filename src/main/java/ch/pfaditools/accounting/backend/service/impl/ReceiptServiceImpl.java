package ch.pfaditools.accounting.backend.service.impl;

import ch.pfaditools.accounting.backend.dao.ReceiptDao;
import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import org.springframework.stereotype.Service;

@Service
public class ReceiptServiceImpl extends BaseServiceImpl<ReceiptEntity, ReceiptEntityFilter> implements ReceiptService {
    public ReceiptServiceImpl(ReceiptDao dao) {
        super(dao);
    }
}
