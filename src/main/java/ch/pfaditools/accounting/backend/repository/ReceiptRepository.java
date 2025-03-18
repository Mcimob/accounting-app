package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends BaseRepository<ReceiptEntity, ReceiptEntityFilter> {
}
