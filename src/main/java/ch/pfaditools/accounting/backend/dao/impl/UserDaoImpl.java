package ch.pfaditools.accounting.backend.dao.impl;

import ch.pfaditools.accounting.backend.dao.UserDao;
import ch.pfaditools.accounting.backend.repository.BaseRepository;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import org.springframework.stereotype.Component;

@Component
public class UserDaoImpl extends BaseDaoImpl<UserEntity, UserEntityFilter> implements UserDao {

    public UserDaoImpl(BaseRepository<UserEntity, UserEntityFilter> repository) {
        super(repository);
    }
}
