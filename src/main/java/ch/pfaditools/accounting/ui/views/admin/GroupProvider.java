package ch.pfaditools.accounting.ui.views.admin;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class GroupProvider extends AbstractBackEndDataProvider<GroupEntity, Void> {

    private final transient GroupService groupService;

    public GroupProvider(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    protected Stream<GroupEntity> fetchFromBackEnd(Query<GroupEntity, Void> query) {
        ServiceResponse<Page<GroupEntity>> response =
                groupService.fetch(PageRequest.of(query.getPage(), query.getPageSize()), new GroupEntityFilter());
        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            return Stream.empty();
        }

        return response.getEntity().get().stream();
    }

    @Override
    protected int sizeInBackEnd(Query<GroupEntity, Void> query) {
        return (int) fetchFromBackEnd(query).count();
    }
}
