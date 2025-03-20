package ch.pfaditools.accounting.ui.views.security;

import ch.pfaditools.accounting.ui.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_ACCESS_DENIED;

@Route(value = ROUTE_ACCESS_DENIED, layout = MainLayout.class)
@PermitAll
public class AccessDeniedView extends VerticalLayout implements HasDynamicTitle {
    public AccessDeniedView() {
        add(new Text(getTranslation("view.accessDenied.text")));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.accessDenied.title");
    }
}
