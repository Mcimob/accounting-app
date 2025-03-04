package ch.pfaditools.accounting.ui.views.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("logout")
@RolesAllowed({"USER", "ADMIN"})
public class LogoutView extends VerticalLayout {
    public LogoutView() {
        UI.getCurrent().getPage().setLocation("/logout");
    }
}
