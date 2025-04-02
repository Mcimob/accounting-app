package ch.pfaditools.accounting.ui.views.entity.receipt;

import ch.pfaditools.accounting.backend.service.FileService;
import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.FileEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.components.CustomUpload;
import ch.pfaditools.accounting.ui.views.entity.AbstractEditEntityView;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import org.vaadin.addons.MoneyField;

import java.io.InputStream;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_RECEIPT;

@Route(value = ROUTE_EDIT_RECEIPT, layout = MainLayout.class)
@PermitAll
public class EditReceiptView extends AbstractEditEntityView<ReceiptEntity, ReceiptEntityFilter> {

    private final transient FileService fileService;

    private TextField titleField;
    private MoneyField amountField;
    private TextArea descriptionField;

    private final FileBuffer buffer = new FileBuffer();
    private final CustomUpload upload = new CustomUpload(buffer);
    private final Div mediaDiv = new Div();

    private FileEntity uploadedFile;
    private FileEntity originalFile;

    public EditReceiptView(FileService fileService, ReceiptService receiptService) {
        super(receiptService);
        this.fileService = fileService;
        setupUpload();
        setupLayout();
    }

    @Override
    protected Component createForm() {
        VerticalLayout uploadLayout = new VerticalLayout(upload, mediaDiv);
        FormLayout layout = new FormLayout(titleField, amountField, descriptionField, uploadLayout);
        layout.setColspan(descriptionField, 2);
        layout.setColspan(uploadLayout, 2);
        layout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        return layout;
    }

    @Override
    protected ReceiptEntity copyEntity(ReceiptEntity entity) {
        return new ReceiptEntity(entity);
    }

    @Override
    protected void setupBinder() {
        binder.forField(titleField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.receipt.title")))
                .bind(ReceiptEntity::getName, ReceiptEntity::setName);
        binder.forField(descriptionField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.receipt.description")))
                .bind(ReceiptEntity::getDescription, ReceiptEntity::setDescription);
        binder.forField(amountField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.receipt.amount")))
                .bind(ReceiptEntity::getAmount, ReceiptEntity::setAmount);
    }


    @Override
    protected boolean beforeSave() {
        if (oldEntity.getFile() == null && uploadedFile == null) {
            showErrorNotification("view.editReceipt.notification.fileRequired");
            return false;
        }

        originalFile = oldEntity.getFile();
        if (uploadedFile != null) {
            uploadedFile.updateCreateModifyFields(SecurityUtils.getAuthenticatedUsername());

            ServiceResponse<FileEntity> fileSaveResponse =
                    fileService.saveWithFile(uploadedFile, buffer.getInputStream());
            if (fileSaveResponse.hasErrorMessages()) {
                showMessagesFromResponse(fileSaveResponse);
                return false;
            }
            fileSaveResponse.getEntity().ifPresent(newEntity::setFile);
        }

        if (SecurityUtils.getAuthenticatedUserGroup() != null) {
            newEntity.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        }
        return true;
    }

    @Override
    protected boolean afterSave() {
        if (originalFile != null && uploadedFile != null) {
            ServiceResponse<FileEntity> deleteResponse = fileService.deleteFile(originalFile);
            if (deleteResponse.hasErrorMessages()) {
                showMessagesFromResponse(deleteResponse);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void setupFields() {
        titleField = new TextField(getTranslation("entity.receipt.title"));
        amountField = new MoneyField(getTranslation("entity.receipt.amount"));
        descriptionField = new TextArea(getTranslation("entity.receipt.description"));

        amountField.setCurrency(SecurityUtils.getGroupCurrencyString());
        amountField.setCurrencyReadOnly(true);
    }

    @Override
    protected boolean afterDelete() {
        fileService.deleteFile(oldEntity.getFile());
        return true;
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
        upload.addFileRemovedListener(event -> {
            mediaDiv.removeAll();
        });
        upload.setAcceptedFileTypes(".pdf", ".png", ".jpg", ".jpeg");
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
    protected ReceiptEntity createEntity() {
        return new ReceiptEntity();
    }

    @Override
    protected void afterNavigation() {
        ServiceResponse<InputStream> contentResponse = fileService.getFileContent(oldEntity.getFile());
        contentResponse.getEntity().ifPresentOrElse(stream ->
                        setMedia(new StreamResource(
                                oldEntity.getFile().getFileName(), () -> stream), oldEntity.getFile().getMimeType()),
                () -> {
                });
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.editReceipt.title");
    }
}
