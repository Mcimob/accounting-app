package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import org.springframework.data.jpa.domain.Specification;

public class GroupEntityFilter extends AbstractFilter<GroupEntity> {

    private String name;

    @Override
    public Specification<GroupEntity> getSpecification() {
        Specification<GroupEntity> specification = super.getSpecification();

        if (name != null) {
            specification = specification.and(hasName(name));
        }

        return specification;
    }

    private Specification<GroupEntity> hasName(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
