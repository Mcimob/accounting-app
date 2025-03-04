package ch.pfaditools.accounting.ui.views.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGIN;

@Route(ROUTE_LOGIN)
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        if (isUserAuthenticated()) {
            UI.getCurrent().navigate("/");
            return;
        }

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");

        add(loginForm);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    private boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }
}
