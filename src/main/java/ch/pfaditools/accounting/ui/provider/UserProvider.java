package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;

public class UserProvider extends AbstractEntityProvider<UserEntity, UserEntityFilter> {

    public UserProvider(UserService service) {
        super(service);
    }

    @Override
    protected UserEntityFilter getFilter() {
        UserEntityFilter filter = new UserEntityFilter();
        filter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        return filter;
    }
}
