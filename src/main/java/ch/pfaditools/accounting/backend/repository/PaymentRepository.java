package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends BaseRepository<PaymentEntity, PaymentEntityFilter> {
}
