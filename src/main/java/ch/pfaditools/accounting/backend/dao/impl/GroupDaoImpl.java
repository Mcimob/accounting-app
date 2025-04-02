package ch.pfaditools.accounting.backend.dao.impl;

import ch.pfaditools.accounting.backend.dao.GroupDao;
import ch.pfaditools.accounting.backend.repository.GroupRepository;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import org.springframework.stereotype.Component;

@Component
public class GroupDaoImpl extends BaseDaoImpl<GroupEntity, GroupEntityFilter> implements GroupDao {

    public GroupDaoImpl(GroupRepository repository) {
        super(repository);
    }
}
