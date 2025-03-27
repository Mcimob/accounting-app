package ch.pfaditools.accounting.backend.dao.impl;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;

public class GroupDaoImplTest extends BaseDaoImplTest<GroupEntity, GroupEntityFilter> {

    @Override
    protected GroupEntity getEntity() {
        return new GroupEntity();
    }

    @Override
    protected GroupEntityFilter getFilter() {
        return new GroupEntityFilter();
    }
}
