package ch.pfaditools.accounting.ui.views.receipt;

import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.logger.HasLogger;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.DesignConstants;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.provider.ReceiptProvider;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import ch.pfaditools.accounting.ui.views.HasNotification;
import ch.pfaditools.accounting.util.AmountUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_USER_STRING;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_RECEIPT;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_RECEIPT_OVERVIEW;
import static ch.pfaditools.accounting.ui.views.AbstractEditEntityView.KEY_ENTITY;

@Route(value = ROUTE_RECEIPT_OVERVIEW, layout = MainLayout.class)
@PermitAll
public class ReceiptOverView extends AbstractNarrowView implements HasLogger, HasNotification, HasDynamicTitle {

    private final transient UserService userService;
    private final ConfigurableFilterDataProvider<ReceiptEntity, Void, ReceiptEntityFilter> filterDataProvider;

    private final ComboBox<UserEntity> userCbx = new ComboBox<>(getTranslation("entity.abstract.createdUser"));
    private final Checkbox unpaidCheck = new Checkbox(getTranslation("view.receipt.unpaid"));
    private final Grid<ReceiptEntity> grid = new Grid<>();

    private final ReceiptEntityFilter filter = new ReceiptEntityFilter();

    public ReceiptOverView(UserService userService, ReceiptProvider receiptProvider) {
        this.userService = userService;
        this.filterDataProvider = receiptProvider.withConfigurableFilter();
        setupLayout();
        setupFilter();
        render();
    }

    private void setupLayout() {
        addClassName(DesignConstants.VIEW);
    }

    private void setupFilter() {
        if (Objects.requireNonNull(SecurityUtils.getCurrentUser()).getRoles().contains(ROLE_USER_STRING)) {
            filter.setCreatedByUser(SecurityUtils.getAuthenticatedUsername());
        } else {
            filter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        }
        refreshFilter();
    }

    private Component createAddButton() {
        Button createButton = new Button(getTranslation("view.receipt.addReceipt"));
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(click -> UI.getCurrent().navigate(ROUTE_EDIT_RECEIPT));

        return createButton;
    }

    private Component createSelectionBar() {
        userCbx.setItems(new UserProvider(userService));
        userCbx.setItemLabelGenerator(UserEntity::getUsername);
        userCbx.addValueChangeListener(event -> {
           filter.setCreatedByUser(Optional.ofNullable(event.getValue()).map(UserEntity::getUsername).orElse(null));
            refreshFilter();
        });
        if (SecurityUtils.isUserInRole(ROLE_USER_STRING)) {
            userCbx.setVisible(false);
        }
        unpaidCheck.addValueChangeListener(event -> {
            filter.setNotPaidBefore(Boolean.TRUE.equals(event.getValue()) ? LocalDateTime.now() : null);
            refreshFilter();
        });
        HorizontalLayout layout = new HorizontalLayout(userCbx, unpaidCheck);
        layout.setAlignItems(FlexComponent.Alignment.BASELINE);
        return layout;
    }

    private Component createGrid() {
        grid.addComponentColumn(this::createGridComponent);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(receipt ->
                    UI.getCurrent().navigate(
                            ROUTE_EDIT_RECEIPT,
                            QueryParameters.of(KEY_ENTITY, receipt.getId().toString())));
        });
        grid.setItems(filterDataProvider);
        grid.setWidthFull();
        return grid;
    }

    private Component createGridComponent(ReceiptEntity receipt) {
        VerticalLayout layout = new VerticalLayout();

        Span title = new Span(receipt.getName());
        title.addClassName(DesignConstants.STYLE_FW_700);

        HorizontalLayout middleLayout = new HorizontalLayout();
        middleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        middleLayout.add(title, new Span("CHF " + AmountUtil.fromAmount(receipt.getAmount())));
        middleLayout.setWidthFull();

        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        bottomLayout.add(new Span(receipt.getCreatedUser()));
        bottomLayout.setWidthFull();

        Icon paidIcon = receipt.getPaidOutAt() == null ? VaadinIcon.CASH.create() :  VaadinIcon.CHECK_CIRCLE.create();
        paidIcon.setColor(receipt.getPaidOutAt() == null ? DesignConstants.CLR_ACCENT : DesignConstants.CLR_REGULAR);

        layout.add(middleLayout, paidIcon, bottomLayout);
        return layout;
    }

    private void refreshFilter() {
        filterDataProvider.setFilter(filter);
        filterDataProvider.refreshAll();
    }

    protected void render() {
        super.render();
        add(createAddButton());
        add(createSelectionBar());
        add(createGrid());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.receipt.title");
    }
}
