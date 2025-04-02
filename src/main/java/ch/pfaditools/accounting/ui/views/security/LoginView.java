package ch.pfaditools.accounting.ui.views.security;

import ch.pfaditools.accounting.ui.components.CustomLogin;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_REGISTER;

@Route(ROUTE_LOGIN)
@AnonymousAllowed
public class LoginView extends AbstractNarrowView implements BeforeEnterObserver {

    private final CustomLogin loginForm = new CustomLogin();

    public LoginView() {
        if (isUserAuthenticated()) {
            UI.getCurrent().navigate("/");
        }
    }

    private Component createLoginComponent() {
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);

        return loginForm;
    }

    private Component createRegisterButton() {
        Button registerButton = new Button(getTranslation("view.login.registerLink"));
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        registerButton.addClickListener(e -> UI.getCurrent().navigate(ROUTE_REGISTER));

        return registerButton;
    }

    private boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @Override
    protected void render() {
        super.render();
        add(createLoginComponent());
        add(createRegisterButton());

    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.login.title");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        System.out.println("BEFORE-EVENT-METHOD-CALLED!");

        // inform the user about an authentication error
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
