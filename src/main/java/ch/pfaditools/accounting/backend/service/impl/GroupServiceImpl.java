package ch.pfaditools.accounting.backend.service.impl;

import ch.pfaditools.accounting.backend.dao.GroupDao;
import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl extends BaseServiceImpl<GroupEntity, GroupEntityFilter> implements GroupService {

    public GroupServiceImpl(GroupDao dao) {
        super(dao);
    }
}
