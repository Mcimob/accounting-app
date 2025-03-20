package ch.pfaditools.accounting.ui.views.receipt;

import ch.pfaditools.accounting.backend.service.FileService;
import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.logger.HasLogger;
import ch.pfaditools.accounting.model.entity.FileEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.components.CustomUpload;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import ch.pfaditools.accounting.ui.views.HasNotification;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;

import java.io.InputStream;
import java.util.Optional;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_RECEIPT;

@Route(value = ROUTE_EDIT_RECEIPT, layout = MainLayout.class)
@PermitAll
public class EditReceiptView extends AbstractNarrowView implements HasLogger, HasNotification, HasUrlParameter<String>,
        HasDynamicTitle {

    private final transient FileService fileService;
    private final transient ReceiptService receiptService;

    private final TextField titleField = new TextField(getTranslation("entity.receipt.title"));
    private final TextField amountField = new TextField(getTranslation("entity.receipt.amount"));
    private final TextArea descriptionField = new TextArea(getTranslation("entity.receipt.description"));

    private final FileBuffer buffer = new FileBuffer();
    private final CustomUpload upload = new CustomUpload(buffer);
    private final Div mediaDiv = new Div();

    private final Button deleteButton = new Button(getTranslation("view.general.delete"));
    private final Button saveButton = new Button(getTranslation("view.general.save"));

    private final Binder<ReceiptEntity> binder = new Binder<>();
    private ReceiptEntity receipt;
    private FileEntity uploadedFile;

    public EditReceiptView(FileService fileService, ReceiptService receiptService) {
        this.fileService = fileService;
        this.receiptService = receiptService;
        setupUpload();
        setupBinder();
        setupButtons();
        setupLayout();
    }

    private Component createForm() {
        VerticalLayout uploadLayout = new VerticalLayout(upload, mediaDiv);
        FormLayout layout = new FormLayout(titleField, amountField, descriptionField, uploadLayout);
        layout.setColspan(descriptionField, 2);
        layout.setColspan(uploadLayout, 2);
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        return layout;
    }

    private Component createButtonsBar() {
        HorizontalLayout buttonsBar = new HorizontalLayout(deleteButton, saveButton);
        buttonsBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonsBar.setWidthFull();

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return buttonsBar;
    }

    private void setupBinder() {
        amountField.setPattern("[0-9]+\\.[0-9]{2}");
        amountField.setPrefixComponent(new Div("CHF"));
        binder.forField(titleField)
                .asRequired()
                .bind(ReceiptEntity::getName, ReceiptEntity::setName);
        binder.forField(descriptionField)
                .asRequired()
                .bind(ReceiptEntity::getDescription, ReceiptEntity::setDescription);
        binder.forField(amountField)
                .asRequired()
                .bind(ReceiptEntity::getAmountString, ReceiptEntity::setAmountString);
    }

    private void setupButtons() {
        saveButton.addClickListener(this::onSaveButtonClick);
        deleteButton.addClickListener(this::onDeleteButtonClick);
    }

    private void onSaveButtonClick(ClickEvent<Button> event) {
        try {
            binder.writeBean(receipt);
        } catch (ValidationException e) {
            getLogger().warn("Validation failed for saving receipt", e);
            return;
        }
        if (receipt.getFile() == null && uploadedFile == null) {
            showErrorNotification(getTranslation("view.editReceipt.notification.fileRequired"));
            return;
        }

        FileEntity originalFile = receipt.getFile();
        if (uploadedFile != null) {
            uploadedFile.updateCreateModifyFields(SecurityUtils.getAuthenticatedUsername());

            ServiceResponse<FileEntity> fileSaveResponse =
                    fileService.saveWithFile(uploadedFile, buffer.getInputStream());
            if (fileSaveResponse.hasErrorMessages()) {
                fileSaveResponse.getErrorMessages().forEach(this::showErrorNotification);
                return;
            }
            fileSaveResponse.getEntity().ifPresent(receipt::setFile);
        }

        receipt.updateCreateModifyFields(SecurityUtils.getAuthenticatedUsername());
        if (SecurityUtils.getAuthenticatedUserGroup() != null) {
            receipt.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        }
        ServiceResponse<ReceiptEntity> response = receiptService.save(receipt);
        if (response.hasErrorMessages()) {
            response.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }
        response.getInfoMessages().forEach(this::showSuccessNotification);

        if (originalFile != null && uploadedFile != null) {
            ServiceResponse<FileEntity> deleteResponse = fileService.deleteFile(originalFile);
            if (deleteResponse.hasErrorMessages()) {
                deleteResponse.getErrorMessages().forEach(this::showErrorNotification);
            }
        }

        UI.getCurrent().getPage().getHistory().back();
    }

    private void onDeleteButtonClick(ClickEvent<Button> event) {
        if (receipt.getId() == null) {
            UI.getCurrent().getPage().getHistory().back();
            return;
        }

        ServiceResponse<ReceiptEntity> receiptResponse = receiptService.delete(receipt);
        if (receiptResponse.hasErrorMessages()) {
            receiptResponse.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }

        fileService.deleteFile(receipt.getFile());
        UI.getCurrent().getPage().getHistory().back();
    }

    private void setupUpload() {
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            String mimeType = event.getMIMEType();

            uploadedFile = new FileEntity();
            uploadedFile.setFileName(fileName);
            uploadedFile.setMimeType(mimeType);

            setMedia(new StreamResource(fileName, buffer::getInputStream), mimeType);
        });
        upload.setAcceptedFileTypes(".pdf", ".png", ".jpg", ".jpeg");

    }

    protected void render() {
        super.render();
        add(createForm());
        add(createButtonsBar());
    }

    private void setupLayout() {
        mediaDiv.setWidthFull();
        upload.setWidthFull();
    }

    private void setMedia(StreamResource resource, String mimeType) {
        mediaDiv.removeAll();
        Component media = switch (mimeType) {
            case "image/jpeg", "image/png" -> createImageComponent(resource);
            case "application/pdf" -> createPdfComponent(resource);
            default -> throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
        };
        mediaDiv.add(media);
    }

    private Component createImageComponent(StreamResource resource) {
        Image image = new Image();
        image.setSrc(resource);
        image.setMaxWidth("100%");
        image.setMaxHeight("100%");
        return image;
    }

    private Component createPdfComponent(StreamResource resource) {
        PdfViewer pdfViewer = new PdfViewer();
        pdfViewer.setSrc(resource);
        pdfViewer.setAddDownloadButton(false);
        return pdfViewer;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        event.getLocation().getQueryParameters().getSingleParameter("receiptId").ifPresentOrElse(receiptId -> {
            long id;
            try {
                id = Long.parseLong(receiptId);
            } catch (NumberFormatException e) {
                getLogger().info("Invalid receipt ID: {}", receiptId);
                UI.getCurrent().getPage().getHistory().back();
                return;
            }
            ServiceResponse<ReceiptEntity> response = receiptService.fetchById(id);
            Optional<ReceiptEntity> receiptOptional = response.getEntity();
            if (response.hasErrorMessages() || receiptOptional.isEmpty()) {
                response.getErrorMessages().forEach(this::showErrorNotification);
                UI.getCurrent().getPage().getHistory().back();
                return;
            }
            receipt = receiptOptional.get();
            binder.readBean(receipt);
            ServiceResponse<InputStream> contentResponse = fileService.getFileContent(receipt.getFile());
            contentResponse.getEntity().ifPresentOrElse(stream ->
                setMedia(new StreamResource(
                        receipt.getFile().getFileName(), () -> stream), receipt.getFile().getMimeType()),
                    () -> {
            });
        }, () -> receipt = new ReceiptEntity());
        render();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.editReceipt.title");
    }
}
