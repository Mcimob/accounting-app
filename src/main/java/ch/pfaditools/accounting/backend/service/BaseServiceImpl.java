package ch.pfaditools.accounting.backend.service;

import ch.pfaditools.accounting.backend.dao.BaseDao;
import ch.pfaditools.accounting.backend.dao.DaoException;
import ch.pfaditools.accounting.backend.dao.VersioningDaoException;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.model.loadtype.HasLoadType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class BaseServiceImpl<T extends AbstractEntity, F extends AbstractFilter<T>> implements BaseService<T, F> {

    public static final String ERROR_GENERAL = "service.base.error.general";

    public static final String ERROR_NULL = "service.base.error.load";

    public static final String ERROR_VERSION = "service.base.error.version";

    private final BaseDao<T, F> dao;

    public BaseServiceImpl(BaseDao<T, F> dao) {
        this.dao = dao;
    }

    protected <R> ServiceResponse<R> handleException(Exception e, String message) {
        ServiceResponse<R> response = new ServiceResponse<>();
        logSevere(message, e);
        response.addErrorMessage(ERROR_GENERAL);
        return response;
    }

    protected <R> boolean handleNullCheck(ServiceResponse<R> response, Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                logWarning("A null object was passed to a service method");
                response.addErrorMessage(ERROR_NULL);
                return true;
            }
        }
        return false;
    }

    protected <R> ServiceResponse<R> handleVersioningException(VersioningDaoException e, ServiceResponse<R> response) {
        logWarning("An exception occurred while saving: ", e);
        response.addErrorMessage(ERROR_VERSION);
        return response;
    }

    @Override
    public ServiceResponse<Page<T>> fetch(Pageable pageable, F filter, HasLoadType loadType) {
        ServiceResponse<Page<T>> response = new ServiceResponse<>();
        if (handleNullCheck(response, pageable, filter, loadType)) {
            return response;
        }

        try {
            Page<T> result = dao.fetch(pageable, filter, loadType);
            response.setEntity(result);
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error fetching entities");
        }
    }

    @Override
    public ServiceResponse<Page<T>> fetch(Pageable pageable, F filter) {
        return fetch(pageable, filter, null);
    }

    @Override
    public ServiceResponse<T> fetchOne(F filter, HasLoadType loadType) {
        ServiceResponse<T> response = new ServiceResponse<>();
        if (handleNullCheck(response, filter,loadType)) {
            return response;
        }

        try {
            Optional<T> result = dao.fetchOne(filter, loadType);
            result.ifPresentOrElse(response::setEntity, () -> response.addErrorMessage("Entity not found"));
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error fetching entity");
        }
    }

    @Override
    public ServiceResponse<T> fetchOne(F filter) {
        return fetchOne(filter, null);
    }

    @Override
    public ServiceResponse<T> fetchById(Long id, HasLoadType loadType) {
        ServiceResponse<T> response = new ServiceResponse<>();
        if (handleNullCheck(response, id, loadType)) {
            return response;
        }

        try {
            Optional<T> result = dao.fetchById(id, loadType);
            result.ifPresentOrElse(response::setEntity, () -> response.addErrorMessage("Entity not found"));
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error fetching entity by ID");
        }
    }

    @Override
    public ServiceResponse<T> fetchById(Long id) {
        return fetchById(id, null);
    }

    @Override
    public ServiceResponse<Boolean> exists(F filter) {
        ServiceResponse<Boolean> response = new ServiceResponse<>();
        if (handleNullCheck(response, filter)) {
            return response;
        }

        try {
            response.setEntity(dao.exists(filter));
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error checking entity existence");
        }
    }

    @Override
    public ServiceResponse<Boolean> existsById(Long id) {
        ServiceResponse<Boolean> response = new ServiceResponse<>();
        if (handleNullCheck(response, id)) {
            return response;
        }

        try {
            response.setEntity(dao.existsById(id));
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error checking entity existence by ID");
        }
    }

    @Override
    public ServiceResponse<Long> count(F filter) {
        ServiceResponse<Long> response = new ServiceResponse<>();
        if (handleNullCheck(response, filter)) {
            return response;
        }

        try {
            response.setEntity(dao.count(filter));
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error counting entities");
        }
    }

    @Override
    public ServiceResponse<T> save(T entity) {
        ServiceResponse<T> response = new ServiceResponse<>();
        if (handleNullCheck(response, entity)) {
            return response;

        }

        try {
            response.setEntity(dao.save(entity).orElse(null));
            return response;
        } catch (VersioningDaoException e) {
            return handleVersioningException(e, response);
        } catch (DaoException e) {
            return handleException(e, "Error saving entity");
        }
    }

    @Override
    public ServiceResponse<Page<T>> saveAll(Iterable<T> entities) {
        ServiceResponse<Page<T>> response = new ServiceResponse<>();
        if (handleNullCheck(response, entities)) {
            return response;
        }

        try {
            response.setEntity(dao.saveAll(entities));
            return response;
        } catch (VersioningDaoException e) {
            return handleVersioningException(e, response);
        } catch (DaoException e) {
            return handleException(e, "Error saving entities");
        }
    }

    @Override
    public ServiceResponse<T> delete(T entity) {
        ServiceResponse<T> response = new ServiceResponse<>();
        if (handleNullCheck(response, entity)) {
            return response;
        }

        try {
            dao.delete(entity);
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error deleting entity");
        }
    }

    @Override
    public ServiceResponse<T> deleteById(Long id) {
        ServiceResponse<T> response = new ServiceResponse<>();
        if (handleNullCheck(response, id)) {
            return response;
        }

        try {
            dao.deleteById(id);
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error deleting entity by ID");
        }
    }

    @Override
    public ServiceResponse<T> deleteAll(Iterable<T> entities) {
        ServiceResponse<T> response = new ServiceResponse<>();
        if (handleNullCheck(response, entities)) {
            return response;
        }

        try {
            dao.deleteAll(entities);
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error deleting multiple entities");
        }
    }
}
