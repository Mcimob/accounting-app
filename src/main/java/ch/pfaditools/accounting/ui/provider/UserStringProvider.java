package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.BaseService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;

import java.util.List;
import java.util.function.BiConsumer;

public class UserStringProvider extends AbstractEntityStringProvider<UserEntity, UserEntityFilter> {

    private UserEntityFilter filter;

    public UserStringProvider(BaseService<UserEntity, UserEntityFilter> service) {
        super(service);
        setupFilter();
    }

    private void setupFilter() {
        UserEntityFilter userFilter = new UserEntityFilter();
        userFilter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        this.filter = userFilter;
    }

    @Override
    public UserEntityFilter getFilter() {
        return filter;
    }

    @Override
    protected List<BiConsumer<UserEntityFilter, String>> getSetters() {
        return List.of(UserEntityFilter::setUsername);
    }
}
