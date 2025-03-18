package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import org.springframework.data.jpa.domain.Specification;

public class ReceiptEntityFilter extends AbstractFilter<ReceiptEntity> {
    @Override
    public Specification<ReceiptEntity> getSpecification() {
        return super.getSpecification();
    }
}
