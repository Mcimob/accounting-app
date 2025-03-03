package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends BaseRepository<GroupEntity, GroupEntityFilter> {
}
