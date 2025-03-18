package ch.pfaditools.accounting.ui.views.receipt;

import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.stream.Stream;

public class UserProvider extends AbstractBackEndDataProvider<UserEntity, String> {

    private final transient UserService userService;

    public UserProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected Stream<UserEntity> fetchFromBackEnd(Query<UserEntity, String> query) {
        Optional<String> filter = query.getFilter();
        UserEntityFilter userFilter = new UserEntityFilter();
        userFilter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        filter.ifPresent(userFilter::setUsername);

        ServiceResponse<Page<UserEntity>> response =
                userService.fetch(PageRequest.of(query.getPage(), query.getPageSize()), userFilter);
        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            return Stream.empty();
        }

        return response.getEntity().get().stream();
    }

    @Override
    protected int sizeInBackEnd(Query<UserEntity, String> query) {
        return (int) fetchFromBackEnd(query).count();
    }
}
