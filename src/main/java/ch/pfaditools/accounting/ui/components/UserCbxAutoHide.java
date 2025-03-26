package ch.pfaditools.accounting.ui.components;

import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.provider.UserStringProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_USER;

public class UserCbxAutoHide extends ComboBox<UserEntity> {

    public UserCbxAutoHide(UserService userService) {
        UserStringProvider provider = new UserStringProvider(userService);
        setItems(provider);
        setItemLabelGenerator(UserEntity::getUsername);
        if (SecurityUtils.isUserInRole(ROLE_USER)) {
            setVisible(false);
        }
        provider.getFilter().setGroup(SecurityUtils.getAuthenticatedUserGroup());
    }

    public void setEmptySelectionAllowed(boolean emptySelectionAllowed) {
        if (!emptySelectionAllowed) {
            setPrefixComponent(null);
        }
        Button button = new Button(VaadinIcon.CLOSE.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        button.addClickListener(e -> setValue(null));
        setPrefixComponent(button);
    }
}
