package ch.pfaditools.accounting.backend.service;

import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends BaseService<UserEntity, UserEntityFilter>, UserDetailsService {
}
