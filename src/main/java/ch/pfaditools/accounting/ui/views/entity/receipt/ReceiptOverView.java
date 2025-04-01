package ch.pfaditools.accounting.ui.views.entity.receipt;

import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.DesignConstants;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.components.UserCbxAutoHide;
import ch.pfaditools.accounting.ui.provider.ReceiptProvider;
import ch.pfaditools.accounting.ui.views.entity.AbstractEntityOverView;
import ch.pfaditools.accounting.util.AmountUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.Objects;
import java.util.Optional;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_USER_STRING;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_RECEIPT;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_RECEIPT_OVERVIEW;

@Route(value = ROUTE_RECEIPT_OVERVIEW, layout = MainLayout.class)
@PermitAll
public class ReceiptOverView extends AbstractEntityOverView<ReceiptEntity, ReceiptEntityFilter> {

    private final transient UserService userService;

    private final Checkbox unpaidCheck = new Checkbox(getTranslation("view.receipt.unpaid"));

    public ReceiptOverView(ReceiptService receiptService, UserService userService) {
        super(new ReceiptProvider(receiptService), ROUTE_EDIT_RECEIPT, "view.receipt.addReceipt");
        this.userService = userService;
    }

    @Override
    protected Component createGrid() {
        Grid.Column<ReceiptEntity> nameColumn = grid.addColumn(ReceiptEntity::getName)
                        .setHeader(getTranslation("entity.receipt.title"));
        grid.addColumn(r -> AmountUtil.fromAmountWithCurrency(r.getAmount()))
                        .setHeader(getTranslation("entity.receipt.amount"));
        grid.addComponentColumn(this::createIcon)
                        .setHeader(getTranslation("entity.receipt.paid"));
        if (SecurityUtils.isUserInAnyRole(ROLE_ADMIN, ROLE_GROUP_ADMIN)) {
            Grid.Column<ReceiptEntity> createdColumn = grid.addColumn(ReceiptEntity::getCreatedUser)
                    .setHeader(getTranslation("entity.abstract.createdUser"));
            UserCbxAutoHide userCbx = new UserCbxAutoHide(userService);
            userCbx.setEmptySelectionAllowed(true);
            addHeaderFilterCell(
                    createdColumn,
                    (f, user) -> f.setCreatedByUser(Optional.ofNullable(user)
                            .map(UserEntity::getUsername).orElse(null)),
                    userCbx);
        }

        new TextField().setWidthFull();
        addHeaderFilterCell(nameColumn, ReceiptEntityFilter::setName, new TextField());

        return grid;
    }

    private Icon createIcon(ReceiptEntity receipt) {
        Icon paidIcon = receipt.getPayment() == null
                ? VaadinIcon.CLOSE_CIRCLE.create()
                :  VaadinIcon.CHECK_CIRCLE.create();
        paidIcon.setColor(receipt.getPayment() == null
                ? DesignConstants.CLR_ACCENT
                : DesignConstants.CLR_REGULAR);
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
