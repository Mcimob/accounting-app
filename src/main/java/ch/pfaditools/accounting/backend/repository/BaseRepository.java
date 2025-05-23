package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BaseRepository<T extends AbstractEntity, F extends AbstractFilter<T>>
        extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    Optional<T> findOne(F filter, HasLoadType loadType);

    Page<T> findAll(F filter, Pageable pageable, HasLoadType loadType);

    Optional<T> findById(Long id, HasLoadType loadType);
}
