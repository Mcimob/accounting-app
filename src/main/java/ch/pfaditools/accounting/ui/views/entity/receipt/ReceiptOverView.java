package ch.pfaditools.accounting.ui.views.entity.receipt;

import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.DesignConstants;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.components.IconToggle;
import ch.pfaditools.accounting.ui.components.UserCbxAutoHide;
import ch.pfaditools.accounting.ui.provider.ReceiptProvider;
import ch.pfaditools.accounting.ui.util.GridUtil;
import ch.pfaditools.accounting.ui.views.entity.AbstractEntityOverView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_USER_STRING;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ALIGN_START;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_WIDTH_FULL;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_RECEIPT;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_RECEIPT_OVERVIEW;

@Route(value = ROUTE_RECEIPT_OVERVIEW, layout = MainLayout.class)
@PermitAll
public class ReceiptOverView extends AbstractEntityOverView<ReceiptEntity, ReceiptEntityFilter> {

    private final transient UserService userService;

    public ReceiptOverView(ReceiptService receiptService, UserService userService) {
        super(new ReceiptProvider(receiptService), ROUTE_EDIT_RECEIPT, "view.receipt.addReceipt");
        this.userService = userService;
    }

    @Override
    protected Component createGrid() {
        Grid.Column<ReceiptEntity> cardColumn = grid.addComponentColumn(ReceiptCard::new);
        Div layout = new Div();
        layout.addClassNames(STYLE_FLEX_COLUMN, STYLE_FLEX_ALIGN_START, STYLE_WIDTH_FULL);

        if (SecurityUtils.isUserInAnyRole(ROLE_ADMIN, ROLE_GROUP_ADMIN)) {
            List<Grid.Column<ReceiptEntity>> columns = createAdminColumns();

            IconToggle gridModeToggle = new IconToggle();
            gridModeToggle.setFalseIcon(VaadinIcon.GRID_BIG_O);
            gridModeToggle.setTrueIcon(VaadinIcon.LINES);
            gridModeToggle.addValueChangeListener(e -> {
                columns.forEach(col -> col.setVisible(e.getValue()));
                cardColumn.setVisible(!e.getValue());
            });
            layout.add(gridModeToggle);
            columns.forEach(col -> col.setVisible(false));
        }

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        layout.add(grid);
        return layout;
    }

    private List<Grid.Column<ReceiptEntity>> createAdminColumns() {
        Grid.Column<ReceiptEntity> nameColumn = grid.addColumn(ReceiptEntity::getName)
                .setHeader(getTranslation("entity.receipt.title"))
                .setSortable(true)
                .setSortProperty("name");
        Grid.Column<ReceiptEntity> amountColumn = grid.addColumn(ReceiptEntity::getAmount)
                .setHeader(getTranslation("entity.receipt.amount"))
                .setSortable(true)
                .setSortProperty("amount");
        Grid.Column<ReceiptEntity> paidColumn = grid.addComponentColumn(rec -> createIcon(rec.getPayment() != null))
                .setHeader(getTranslation("entity.receipt.paid"));
        Grid.Column<ReceiptEntity> createdColumn = grid.addColumn(ReceiptEntity::getCreatedUser)
                .setHeader(getTranslation("entity.abstract.createdUser"))
                .setSortable(true)
                .setSortProperty("createdUser");
        Grid.Column<ReceiptEntity> createdTimeColumn = grid.addColumn(rec -> rec.getCreatedDateTimeString(getLocale()))
                .setHeader(getTranslation("entity.abstract.createdDateTime"))
                .setSortable(true)
                .setSortProperty("createdDateTime");

        GridUtil.addHeaderFilterCell(grid,
                filter,
                filterDataProvider,
                nameColumn,
                ReceiptEntityFilter::setName,
                new TextField());
        GridUtil.addHeaderFilterCell(grid,
                filter,
                filterDataProvider,
                paidColumn,
                ReceiptEntityFilter::setPaidOut,
                createPaidSelect());
        UserCbxAutoHide userCbx = new UserCbxAutoHide(userService);
        userCbx.setEmptySelectionAllowed(true);
        GridUtil.addHeaderFilterCell(grid, filter, filterDataProvider,
                createdColumn,
                (f, user) -> f.setCreatedByUser(Optional.ofNullable(user)
                        .map(UserEntity::getUsername).orElse(null)),
                userCbx);

        return List.of(
                nameColumn,
                amountColumn,
                paidColumn,
                createdColumn,
                createdTimeColumn
        );
    }

    private HasValue<?, Boolean> createPaidSelect() {
        Select<Boolean> paidSelect = new Select<>();
        paidSelect.setItems(List.of(Boolean.TRUE, Boolean.FALSE));
        paidSelect.setEmptySelectionAllowed(true);
        paidSelect.setRenderer(new ComponentRenderer<Component, Boolean>(this::createIcon));
        paidSelect.setWidth("calc(var(--lumo-icon-size-m) * 3)");

        return paidSelect;
    }

    private Icon createIcon(Boolean paid) {
        Icon paidIcon = paid
                ? VaadinIcon.CHECK_CIRCLE.create()
                : VaadinIcon.CLOSE_CIRCLE.create();
        paidIcon.setColor(paid
                ? DesignConstants.CLR_REGULAR
                : DesignConstants.CLR_ACCENT);
        return paidIcon;
    }

    @Override
    protected ReceiptEntityFilter getBaseFilter() {
        ReceiptEntityFilter filter = new ReceiptEntityFilter();
        if (Objects.requireNonNull(SecurityUtils.getCurrentUser()).getRoles().contains(ROLE_USER_STRING)) {
            filter.setCreatedByUser(SecurityUtils.getAuthenticatedUsername());
        } else {
            filter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        }

        return filter;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.receipt.title");
    }
}
