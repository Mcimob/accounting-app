package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseRepository<T extends AbstractEntity, F extends AbstractFilter<T>> extends JpaRepository<T, Long> {

    public Optional<T> findOne(F filter, HasLoadType loadType);

    public Page<T> findAll(F filter, Pageable pageable, HasLoadType loadType);

    Optional<T> findById(Long id, HasLoadType loadType);
}
