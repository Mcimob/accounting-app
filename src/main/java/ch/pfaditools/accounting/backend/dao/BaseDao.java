package ch.pfaditools.accounting.backend.dao;

import ch.pfaditools.accounting.backend.dao.exception.DaoException;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BaseDao<T extends AbstractEntity, F extends AbstractFilter<T>> {

    Page<T> fetch(Pageable pageable, F filter, HasLoadType loadType) throws DaoException;

    Page<T> fetch(Pageable pageable, F filter) throws DaoException;

    Optional<T> fetchOne(F filter, HasLoadType loadType) throws DaoException;

    Optional<T> fetchOne(F filter) throws DaoException;

    Optional<T> fetchById(Long id, HasLoadType loadType) throws DaoException;

    Optional<T> fetchById(Long id) throws DaoException;

    boolean exists(F filter) throws DaoException;

    boolean existsById(Long id) throws DaoException;

    long count(F filter) throws DaoException;

    Optional<T> save(T entity) throws DaoException;

    Page<T> saveAll(Iterable<T> entities) throws DaoException;

    void delete(T entity) throws DaoException;

    void deleteById(Long id) throws DaoException;

    void deleteAll(Iterable<T> entities) throws DaoException;
}
