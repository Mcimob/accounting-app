package ch.pfaditools.accounting.ui.views.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("login")
@PageTitle("Login | MyApp")
@PermitAll
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
