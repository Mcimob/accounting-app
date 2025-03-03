package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import org.springframework.data.jpa.domain.Specification;

public class GroupEntityFilter extends AbstractFilter<GroupEntity> {

    @Override
    public Specification<GroupEntity> getSpecification() {
        return Specification.where(null);
    }
}
