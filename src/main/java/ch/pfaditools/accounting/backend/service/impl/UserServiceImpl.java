package ch.pfaditools.accounting.backend.service.impl;

import ch.pfaditools.accounting.backend.dao.UserDao;
import ch.pfaditools.accounting.backend.dao.exception.DaoException;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserEntity, UserEntityFilter> implements UserService {

    private final UserDao dao;

    public UserServiceImpl(UserDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntityFilter filter = new UserEntityFilter();
        filter.setUsername(username);
        try {
            Optional<UserEntity> user = dao.fetchOne(filter);
            return user.orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
        } catch (DaoException e) {
            logWarning("User %s not found".formatted(username), e);
            throw new UsernameNotFoundException("User %s not found".formatted(username));
        }
    }
}
