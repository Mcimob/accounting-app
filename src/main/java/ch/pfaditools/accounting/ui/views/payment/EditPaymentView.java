package ch.pfaditools.accounting.ui.views.payment;

import ch.pfaditools.accounting.backend.service.PaymentService;
import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.PaymentEntityFilter;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.provider.ReceiptStringProvider;
import ch.pfaditools.accounting.ui.views.AbstractEditEntityView;
import ch.pfaditools.accounting.util.AmountUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.LocalDateTime;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_PAYMENT;

@Route(value = ROUTE_EDIT_PAYMENT, layout = MainLayout.class)
@RolesAllowed({ROLE_ADMIN, ROLE_GROUP_ADMIN})
public class EditPaymentView extends AbstractEditEntityView<PaymentEntity, PaymentEntityFilter> {

    private final ReceiptStringProvider provider;

    private TextField titleField;
    private TextArea descriptionField;
    private TextField amountField;
    private MultiSelectComboBox<ReceiptEntity> receiptCbx;

    public EditPaymentView(PaymentService paymentService, ReceiptStringProvider receiptProvider) {
        super(paymentService);
        this.provider = receiptProvider;
        setupReceiptCbx();
    }

    private void setupReceiptCbx() {
        provider.getFilter().setNotPaidBefore(LocalDateTime.now());

        receiptCbx.setItems(provider);
        receiptCbx.setItemLabelGenerator(receipt -> "%s | CHF %s | %s".formatted(
                receipt.getName(),
                AmountUtil.fromAmount(receipt.getAmount()),
                receipt.getCreatedUser()));

        receiptCbx.addValueChangeListener(event -> {
            amountField.setValue(AmountUtil.fromAmount(event.getValue().stream()
                    .map(ReceiptEntity::getAmount)
                    .reduce(0D, Double::sum)));
        });
    }

    @Override
    protected void setupFields() {
        titleField = new TextField(getTranslation("entity.payment.title"));
        descriptionField = new TextArea(getTranslation("entity.payment.description"));
        amountField = new TextField(getTranslation("entity.receipt.amount"));
        receiptCbx = new MultiSelectComboBox<>(getTranslation("entity.payment.receipts"));

        amountField.setPrefixComponent(new Div("CHF"));
    }

    @Override
    protected void setupBinder() {
        binder.forField(titleField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.payment.title")))
                .bind(PaymentEntity::getTitle, PaymentEntity::setTitle);
        binder.forField(descriptionField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.payment.description")))
                .bind(PaymentEntity::getDescription, PaymentEntity::setDescription);
        binder.forField(amountField)
                .bindReadOnly(payment ->
                        AmountUtil.fromAmount(payment.getReceipts().stream()
                                .map(ReceiptEntity::getAmount)
                                .reduce(0D, Double::sum)));
        binder.forField(receiptCbx)
                .bind(PaymentEntity::getReceipts, PaymentEntity::setReceipts);
    }

    @Override
    protected Component createForm() {
        FormLayout layout = new FormLayout(titleField, amountField, descriptionField, receiptCbx);
        layout.setColspan(descriptionField, 2);
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        return layout;
    }

    @Override
    protected PaymentEntity createEntity() {
        return new PaymentEntity();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.editPayment.title");
    }
}
