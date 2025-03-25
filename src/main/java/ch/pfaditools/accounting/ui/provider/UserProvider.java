package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.BaseService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;

import java.util.List;
import java.util.function.BiConsumer;

public class UserProvider extends AbstractEntityStringProvider<UserEntity, UserEntityFilter> {

    public UserProvider(BaseService<UserEntity, UserEntityFilter> service) {
        super(service);
    }

    @Override
    protected UserEntityFilter getFilter() {
        UserEntityFilter userFilter = new UserEntityFilter();
        userFilter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        return userFilter;
    }

    @Override
    protected List<BiConsumer<UserEntityFilter, String>> getSetters() {
        return List.of(UserEntityFilter::setUsername);
    }
}
