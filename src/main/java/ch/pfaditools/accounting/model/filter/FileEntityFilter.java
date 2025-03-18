package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.FileEntity;
import org.springframework.data.jpa.domain.Specification;

public class FileEntityFilter extends AbstractFilter<FileEntity> {
    @Override
    public Specification<FileEntity> getSpecification() {
        return super.getSpecification();
    }
}
