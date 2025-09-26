package ch.pfaditools.accounting.ui.views.entity.payment;

import ch.pfaditools.accounting.backend.service.PaymentService;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.components.CardDetailGrid;
import ch.pfaditools.accounting.ui.provider.PaymentProvider;
import ch.pfaditools.accounting.ui.views.entity.AbstractEntityOverView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_PAYMENT;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_PAYMENT_OVERVIEW;

@Route(value = ROUTE_PAYMENT_OVERVIEW, layout = MainLayout.class)
@RolesAllowed({ROLE_ADMIN, ROLE_GROUP_ADMIN})
public class PaymentOverView extends AbstractEntityOverView<PaymentEntity, PaymentEntityFilter> {

    public PaymentOverView(PaymentService paymentService) {
        super(new PaymentProvider(paymentService), ROUTE_EDIT_PAYMENT, "view.payment.addButton");
    }

    @Override
    protected Component createGrid() {
        Grid.Column<PaymentEntity> cardColumn = grid.addComponentColumn(PaymentCard::new);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);

        if (SecurityUtils.isUserInAnyRole(ROLE_ADMIN)) {
            return new CardDetailGrid<>(grid, createAdminColumns(), cardColumn);
        }

        return grid;
    }

    private List<Grid.Column<PaymentEntity>> createAdminColumns() {
        Grid.Column<PaymentEntity> titleColumn = grid.addColumn(PaymentEntity::getTitle)
                .setHeader(getTranslation("entity.payment.title"))
                .setSortable(true)
                .setSortProperty("title");
        Grid.Column<PaymentEntity> receiptsAmountColumn = grid.addColumn(PaymentEntity::getReceiptsAmount)
                .setHeader(getTranslation("entity.receipt.amount"))
                .setSortable(true)
                .setSortProperty("receiptsAmount");
        Grid.Column<PaymentEntity> receiptsColumn = grid.addComponentColumn(p -> {
            Details details = new Details("%s %s".formatted(
                    p.getReceipts().size(), getTranslation("entity.payment.receipts")));
            p.getReceipts().stream().map(ReceiptEntity::getName).map(Div::new).forEach(details::add);
            return details;
        }).setHeader(getTranslation("entity.payment.receipts"));
        Grid.Column<PaymentEntity> createdColumn = grid.addColumn(pay -> pay.getCreatedDateTimeString(getLocale()))
                .setHeader(getTranslation("entity.abstract.createdDateTime"))
                .setSortable(true)
                .setSortProperty("createdDateTime");

        return List.of(
                titleColumn,
                receiptsAmountColumn,
                receiptsColumn,
                createdColumn);
    }

    @Override
    protected PaymentEntityFilter getBaseFilter() {
        PaymentEntityFilter filter = new PaymentEntityFilter();
        return filter;
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.payment.title");
    }
}
