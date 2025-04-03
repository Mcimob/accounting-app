package ch.pfaditools.accounting.ui.views.entity.payment;

import ch.pfaditools.accounting.backend.service.PaymentService;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.provider.PaymentProvider;
import ch.pfaditools.accounting.ui.views.entity.AbstractEntityOverView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

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
        grid.addColumn(PaymentEntity::getTitle)
                .setHeader(getTranslation("entity.payment.title"))
                .setSortable(true)
                .setSortProperty("title");
        grid.addColumn(PaymentEntity::getReceiptsAmount)
                .setHeader(getTranslation("entity.receipt.amount"))
                .setSortable(true)
                .setSortProperty("receiptsAmount");
        grid.addComponentColumn(p -> {
            Details details = new Details("%s %s".formatted(
                    p.getReceipts().size(), getTranslation("entity.payment.receipts")));
                    p.getReceipts().stream().map(ReceiptEntity::getName).map(Div::new).forEach(details::add);
                    return details;
        }).setHeader(getTranslation("entity.payment.receipts"));
        grid.addColumn(pay -> pay.getCreatedDateTimeString(getLocale()))
                .setHeader(getTranslation("entity.abstract.createdDateTime"))
                .setSortable(true)
                .setSortProperty("createdDateTime");

        return grid;
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
