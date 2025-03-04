package ch.pfaditools.accounting.ui.views.security;

import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_REGISTER;

@Route(ROUTE_REGISTER)
@PageTitle("Register | MyApp")
@PermitAll
public class RegisterView extends VerticalLayout {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public RegisterView(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        VerticalLayout layout = new VerticalLayout();
        layout.setMaxWidth("400px");

        H1 title = new H1("Register");
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");


        Button registerButton = new Button("Register", event -> registerUser(
                usernameField.getValue(),
                passwordField.getValue(),
                confirmPasswordField.getValue(),
                "USER"
        ));
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        usernameField.setWidthFull();
        passwordField.setWidthFull();
        confirmPasswordField.setWidthFull();
        registerButton.setWidthFull();

        layout.add(title, usernameField, passwordField, confirmPasswordField, registerButton);
        add(layout);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    private void registerUser(String username, String password, String confirmPassword, String role) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Notification.show("All fields are required", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            Notification.show("Passwords do not match", 3000, Notification.Position.MIDDLE);
            return;
        }

        UserEntityFilter filter = new UserEntityFilter();
        filter.setUsername(username);
        ServiceResponse<UserEntity> response = userService.fetchOne(filter);
        if (response.getEntity().isPresent()) {
            Notification.show("Username already exists", 3000, Notification.Position.MIDDLE);
            return;
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoles(Set.of(role));
        newUser.updateCreateModifyFields("SYSTEM");

        ServiceResponse<UserEntity> saveResponse = userService.save(newUser);
        if (saveResponse.getEntity().isPresent()) {
            Notification.show("Registration successful! You can now log in.", 3000, Notification.Position.MIDDLE);
            UI.getCurrent().navigate("login");
        } else {
            Notification.show("Registration failed. Try again later.", 3000, Notification.Position.MIDDLE);
        }
    }
}
