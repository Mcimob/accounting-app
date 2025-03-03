package ch.pfaditools.accounting.backend.service;

import ch.pfaditools.accounting.logger.HasLogger;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<T extends AbstractEntity, F extends AbstractFilter<T>> extends HasLogger {

    ServiceResponse<Page<T>> fetch(Pageable pageable, F filter, HasLoadType loadType);

    ServiceResponse<Page<T>> fetch(Pageable pageable, F filter);

    ServiceResponse<T> fetchOne(F filter, HasLoadType loadType);

    ServiceResponse<T> fetchOne(F filter);

    ServiceResponse<T> fetchById(Long id, HasLoadType loadType);

    ServiceResponse<T> fetchById(Long id);

    ServiceResponse<Boolean> exists(F filter);

    ServiceResponse<Boolean> existsById(Long id);

    ServiceResponse<Long> count(F filter);

    ServiceResponse<T> save(T entity);

    ServiceResponse<Page<T>> saveAll(Iterable<T> entities);

    ServiceResponse<T> delete(T entity);

    ServiceResponse<T> deleteById(Long id);

    ServiceResponse<T> deleteAll(Iterable<T> entities);

}
