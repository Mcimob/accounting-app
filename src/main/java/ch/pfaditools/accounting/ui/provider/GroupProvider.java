package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import org.springframework.stereotype.Component;

@Component
public class GroupProvider extends AbstractEntityProvider<GroupEntity, GroupEntityFilter> {

    public GroupProvider(GroupService groupService) {
        super(groupService);
    }

    @Override
    protected GroupEntityFilter getFilter() {
        return new GroupEntityFilter();
    }
}
