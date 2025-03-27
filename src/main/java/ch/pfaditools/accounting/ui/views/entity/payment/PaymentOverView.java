package ch.pfaditools.accounting.ui.views.entity.payment;

import ch.pfaditools.accounting.backend.service.PaymentService;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.provider.PaymentProvider;
import ch.pfaditools.accounting.ui.views.entity.AbstractEntityOverView;
import ch.pfaditools.accounting.util.AmountUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_PAYMENT;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_PAYMENT_OVERVIEW;

@Route(value = ROUTE_PAYMENT_OVERVIEW, layout = MainLayout.class)
@RolesAllowed({ROLE_ADMIN, ROLE_GROUP_ADMIN})
public class PaymentOverView extends AbstractEntityOverView<PaymentEntity, PaymentEntityFilter> {

    public PaymentOverView(PaymentService paymentService) {
        super(new PaymentProvider(paymentService), ROUTE_EDIT_PAYMENT, "view.payment.addButton");
        render();
    }

    @Override
    protected Component createGrid() {
        Grid.Column<PaymentEntity> titleColumn = grid.addColumn(PaymentEntity::getTitle)
                .setHeader(getTranslation("entity.payment.title"));
        grid.addColumn(p -> Optional.of(p)
                        .map(PaymentEntity::getReceipts)
                        .map(AmountUtil::getAmountSum)
                        .map(AmountUtil::fromAmount)
                        .orElse(""))
                .setHeader(getTranslation("entity.receipt.amount"));
        grid.addComponentColumn(p -> {
            Details details = new Details("%s %s".formatted(
                    p.getReceipts().size(), getTranslation("entity.payment.receipts")));
            p.getReceipts().stream().map(ReceiptEntity::getName).map(Div::new).forEach(details::add);
            return details;
        })
                .setHeader(getTranslation("entity.payment.receipts"));

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
