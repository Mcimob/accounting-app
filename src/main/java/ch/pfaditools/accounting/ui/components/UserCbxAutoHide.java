package ch.pfaditools.accounting.ui.components;

import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.provider.UserProvider;
import com.vaadin.flow.component.combobox.ComboBox;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_USER_STRING;

@Scope("prototype")
@Component
public class UserCbxAutoHide extends ComboBox<UserEntity> {

    public UserCbxAutoHide(UserService userService) {
        setItems(new UserProvider(userService));
        setItemLabelGenerator(UserEntity::getUsername);
        if (SecurityUtils.isUserInRole(ROLE_USER_STRING)) {
            setVisible(false);
        }
    }
}
