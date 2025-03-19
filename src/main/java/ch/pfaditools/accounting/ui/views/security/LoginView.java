package ch.pfaditools.accounting.ui.views.security;

import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_REGISTER;

@Route(ROUTE_LOGIN)
@AnonymousAllowed
public class LoginView extends AbstractNarrowView {

    public LoginView() {
        if (isUserAuthenticated()) {
            UI.getCurrent().navigate("/");
            return;
        }

        super.render();

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
        add(loginForm);

        Button registerButton = new Button("Don't have an account yet? Register here");
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        registerButton.addClickListener(e -> UI.getCurrent().navigate(ROUTE_REGISTER));
        add(registerButton);

        setSizeFull();
    }

    private boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
