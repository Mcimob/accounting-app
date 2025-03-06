package ch.pfaditools.accounting.ui.views.security.register;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.logger.HasLogger;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import ch.pfaditools.accounting.ui.views.HasNotification;
import ch.pfaditools.accounting.ui.views.security.AbstractSecurityView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_USER;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_REGISTER;

@Route(ROUTE_REGISTER)
@AnonymousAllowed
public class RegisterView extends AbstractSecurityView implements HasLogger, HasNotification {

    private final UserService userService;
    private final GroupService groupService;
    private final PasswordEncoder passwordEncoder;

    private Binder<UserWithCodeAndGroup> binder;

    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField groupField;
    private PasswordField codeField;

    private final UserWithCodeAndGroup userWithCodeAndGroup = new UserWithCodeAndGroup();

    public RegisterView(UserService userService, GroupService groupService, PasswordEncoder passwordEncoder) {
        super();
        this.userService = userService;
        this.groupService = groupService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void setupBinder() {
        binder = new Binder<>();
        binder.forField(usernameField)
                .asRequired("Required")
                .bind(u -> u.getUser().getUsername(), (u, val) -> u.getUser().setUsername(val));
        binder.forField(passwordField)
                .asRequired("Required")
                .bind(u -> u.getUser().getPassword(), (u, val) -> u.getUser().setPassword(val));
        binder.forField(confirmPasswordField)
                .asRequired("Required")
                .bind(u -> u.getUser().getPassword(), (u, val) -> u.getUser().setPassword(val));
        binder.forField(groupField)
                .asRequired("Required")
                .bind(UserWithCodeAndGroup::getGroupName, UserWithCodeAndGroup::setGroupName);
        binder.forField(codeField)
                .asRequired("Required")
                .bind(UserWithCodeAndGroup::getCode, UserWithCodeAndGroup::setCode);

        binder.setBean(userWithCodeAndGroup);
    }

    private void registerUser(ClickEvent<Button> clickEvent) {
        try {
            binder.writeBean(userWithCodeAndGroup);
        } catch (ValidationException e) {
            logInfo("Validation failed for some fields", e);
            return;
        }

        GroupEntityFilter groupFilter = new GroupEntityFilter();
        groupFilter.setName(userWithCodeAndGroup.getGroupName());
        ServiceResponse<GroupEntity> groupResponse = groupService.fetchOne(groupFilter);
        Optional<GroupEntity> group = groupResponse.getEntity();
        if (groupResponse.hasErrorMessages() || group.isEmpty()) {
            groupResponse.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }

        UserEntity newUser = userWithCodeAndGroup.getUser();

        boolean matchesGroupAdminCode = passwordEncoder.matches(
                userWithCodeAndGroup.getCode(), group.get().getGroupAdminCode());
        boolean matchesGroupUserCode = passwordEncoder.matches(
                userWithCodeAndGroup.getCode(), group.get().getGroupCode());

        if (matchesGroupAdminCode) {
            newUser.getRoles().add(ROLE_GROUP_ADMIN);
        } else if (matchesGroupUserCode) {
            newUser.getRoles().add(ROLE_USER);
        } else {
            showWarningNotification("Invalid group code");
            return;
        }
        newUser.setGroup(group.get());

        UserEntityFilter userFilter = new UserEntityFilter();
        userFilter.setUsername(newUser.getUsername());
        ServiceResponse<UserEntity> userResponse = userService.fetchOne(userFilter);
        if (userResponse.hasErrorMessages()) {
            userResponse.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }
        if (userResponse.getEntity().isPresent()) {
            showWarningNotification("User already exists");
            return;
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.updateCreateModifyFields("REGISTER");
        ServiceResponse<UserEntity> saveResponse = userService.save(newUser);
        if (saveResponse.hasErrorMessages()) {
            saveResponse.getErrorMessages().forEach(this::showErrorNotification);
        } else {
            showSuccessNotification("Registration successful! You can now log in");
            UI.getCurrent().navigate("login");
        }
    }

    @Override
    protected Component createHeader() {
        return new H1("Register");
    }

    @Override
    protected Component createContent() {
        usernameField = new TextField("Username");
        passwordField = new PasswordField("Password");
        confirmPasswordField = new PasswordField("Confirm Password");
        groupField = new TextField("Group");
        codeField = new PasswordField("Code");

        Button registerButton = new Button("Register", this::registerUser);
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return new Div(
                new FormLayout(
                        usernameField,
                        passwordField,
                        confirmPasswordField,
                        groupField,
                        codeField),
                registerButton);
    }
}
