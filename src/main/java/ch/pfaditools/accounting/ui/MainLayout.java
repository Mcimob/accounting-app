package ch.pfaditools.accounting.ui;

import ch.pfaditools.accounting.security.SecurityUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_PAYMENT_OVERVIEW;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_RECEIPT_OVERVIEW;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        Button logoutButton = new Button();
        logoutButton.setIcon(VaadinIcon.EXIT_O.create());
        logoutButton.addClickListener(e -> {
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(),
                    null,
                    null
            );

            UI.getCurrent().getPage().setLocation(ROUTE_LOGIN); // Redirect to login page after logout
        });
        logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout leftLayout = new HorizontalLayout(toggle);
        leftLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        HorizontalLayout rightLayout = new HorizontalLayout(logoutButton);
        rightLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        HorizontalLayout layout = new HorizontalLayout(leftLayout, rightLayout);
        layout.setMargin(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setWidthFull();

        addToNavbar(true, layout);
    }

    private void addDrawerContent() {
        Span appName = new Span("My App");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (SecurityUtils.isUserInRole(ROLE_ADMIN)) {
            nav.addItem(new SideNavItem(
                    getTranslation("view.admin.title"),
                    ROUTE_ADMIN,
                    VaadinIcon.COGS.create()));
        }
        if (SecurityUtils.isUserInAnyRole(ROLE_ADMIN, ROLE_GROUP_ADMIN)) {
            nav.addItem(new SideNavItem(
                    getTranslation("view.payment.title"),
                    ROUTE_PAYMENT_OVERVIEW,
                    VaadinIcon.MONEY.create()));
        }
        nav.addItem(new SideNavItem(
                getTranslation("view.receipt.title"),
                ROUTE_RECEIPT_OVERVIEW,
                VaadinIcon.RECORDS.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
    }
}
