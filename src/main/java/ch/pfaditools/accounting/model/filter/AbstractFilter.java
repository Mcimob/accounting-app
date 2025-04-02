package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

public abstract class AbstractFilter<T extends AbstractEntity> implements Serializable {

    private String createdByUser;

    public Specification<T> getSpecification() {
        Specification<T> spec = Specification.where(null);
        if (createdByUser != null) {
            spec = spec.and(isCreatedByUser());
        }
        return spec;
    }

    private Specification<T> isCreatedByUser() {
        return (root, query, cb) -> cb.equal(root.get("createdUser"), createdByUser);
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }
}
