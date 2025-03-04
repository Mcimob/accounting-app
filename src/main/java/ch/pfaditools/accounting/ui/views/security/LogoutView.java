package ch.pfaditools.accounting.ui.views.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGOUT;

@Route(ROUTE_LOGOUT)
@RolesAllowed({"USER", "ADMIN"})
public class LogoutView extends VerticalLayout {
    public LogoutView() {
        UI.getCurrent().getPage().setLocation("/logout");
    }
}
