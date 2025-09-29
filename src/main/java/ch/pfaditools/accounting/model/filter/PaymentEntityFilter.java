package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import org.springframework.data.jpa.domain.Specification;

public class PaymentEntityFilter extends AbstractFilter<PaymentEntity> {

    private GroupEntity group;

    @Override
    public Specification<PaymentEntity> getSpecification() {
        Specification<PaymentEntity> spec = super.getSpecification();

        if (group != null) {
            spec = spec.and(belongsToGroup());
        }

        return spec;
    }

    private Specification<PaymentEntity> belongsToGroup() {
        return (root, query, cb) ->
                cb.equal(root.get("group"), group);
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }
}
