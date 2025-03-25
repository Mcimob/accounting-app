package ch.pfaditools.accounting.ui.views.payment;

import ch.pfaditools.accounting.backend.service.PaymentService;
import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
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
import org.springframework.data.domain.Page;

import java.util.HashSet;
import java.util.Set;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_PAYMENT;

@Route(value = ROUTE_EDIT_PAYMENT, layout = MainLayout.class)
@RolesAllowed({ROLE_ADMIN, ROLE_GROUP_ADMIN})
public class EditPaymentView extends AbstractEditEntityView<PaymentEntity, PaymentEntityFilter> {

    private final ReceiptStringProvider provider;
    private final ReceiptService receiptService;

    private TextField titleField;
    private TextArea descriptionField;
    private TextField amountField;
    private MultiSelectComboBox<ReceiptEntity> receiptCbx;

    public EditPaymentView(
            PaymentService paymentService,
            ReceiptStringProvider receiptProvider,
            ReceiptService receiptService) {
        super(paymentService);
        this.provider = receiptProvider;
        this.receiptService = receiptService;
        setupReceiptCbx();
    }

    private void setupReceiptCbx() {
        provider.getFilter().setPaidOut(false);
        receiptCbx.setItems(provider);
        receiptCbx.setItemLabelGenerator(receipt -> "%s | CHF %s | %s".formatted(
                receipt.getName(),
                AmountUtil.fromAmount(receipt.getAmount()),
                receipt.getCreatedUser()));

        receiptCbx.setAutoExpand(MultiSelectComboBox.AutoExpandMode.VERTICAL);

        receiptCbx.addValueChangeListener(event -> {
            if (!event.isFromClient()) {
                return;
            }
            Set<ReceiptEntity> newReceipts = event.getValue();
            Set<ReceiptEntity> oldReceipts = event.getOldValue();
            amountField.setValue(AmountUtil.fromAmount(newReceipts.stream()
                    .map(ReceiptEntity::getAmount)
                    .reduce(0L, Long::sum)));

            if (oldEntity.getId() == null) {
                return;
            }

            Set<ReceiptEntity> receiptsToRemove = new HashSet<>(oldReceipts);
            receiptsToRemove.removeAll(newReceipts);

            if (!receiptsToRemove.isEmpty()) {
                receiptsToRemove.forEach(receipt -> receipt.setPayment(null));
                ServiceResponse<Page<ReceiptEntity>> response = receiptService.saveAll(receiptsToRemove);
                showMessagesFromResponse(response);
                provider.refreshAll();
                if (response.hasErrorMessages()) {
                    receiptCbx.setValue(oldReceipts);
                }
            }

            Set<ReceiptEntity> receiptsToAdd = new HashSet<>(newReceipts);
            receiptsToAdd.removeAll(oldReceipts);
            if (!receiptsToAdd.isEmpty()) {
                receiptsToAdd.forEach(receipt -> receipt.setPayment(oldEntity));
                ServiceResponse<Page<ReceiptEntity>> response = receiptService.saveAll(receiptsToAdd);
                showMessagesFromResponse(response);
                provider.refreshAll();
                if (response.hasErrorMessages()) {
                    receiptCbx.setValue(oldReceipts);
                }
            }

        });
    }

    @Override
    protected boolean beforeDelete() {
        Set<ReceiptEntity> oldReceipts = oldEntity.getReceipts();
        oldReceipts.forEach(receipt -> receipt.setPayment(null));
        ServiceResponse<Page<ReceiptEntity>> response = receiptService.saveAll(oldReceipts);
        showMessagesFromResponse(response);
        return !response.hasErrorMessages();
    }

    @Override
    protected boolean afterSave() {
        if (oldEntity.getId() == null) {
            ServiceResponse<Page<ReceiptEntity>> response = receiptService.saveAll(newEntity.getReceipts());
            showMessagesFromResponse(response);
            return !response.hasErrorMessages();
        }
        return true;
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
                                .reduce(0L, Long::sum)));
        binder.forField(receiptCbx)
                .bind(PaymentEntity::getReceipts, (p, receipts) -> {
                    p.setReceipts(receipts);
                    receipts.forEach(r -> r.setPayment(p));
                });
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
    protected PaymentEntity copyEntity(PaymentEntity entity) {
        return new PaymentEntity(entity);
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
