package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, UserEntityFilter> {
}
