package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

public abstract class AbstractFilter<T extends AbstractEntity> implements Serializable {

    public abstract Specification<T> getSpecification();

}
