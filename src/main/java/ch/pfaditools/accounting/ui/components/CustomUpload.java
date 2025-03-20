package ch.pfaditools.accounting.ui.components;

import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;


public class CustomUpload extends Upload {

    public CustomUpload() {
        setI18n(translate());
    }

    public CustomUpload(Receiver receiver) {
        this();
        setReceiver(receiver);
    }

    private UploadI18N translate() {
        return new UploadI18N()
                .setDropFiles(new UploadI18N.DropFiles()
                    .setOne(getTranslation("component.upload.dropFiles.one"))
                    .setMany(getTranslation("component.upload.dropFiles.many")))
                .setAddFiles(new UploadI18N.AddFiles()
                    .setOne(getTranslation("component.upload.addFiles.one"))
                    .setMany(getTranslation("component.upload.addFiles.many")))
                .setError(new UploadI18N.Error()
                    .setTooManyFiles(getTranslation("component.upload.error.tooManyFiles"))
                    .setFileIsTooBig(getTranslation("component.upload.error.fileIsTooBig"))
                    .setIncorrectFileType(getTranslation("component.upload.error.incorrectFileType")))
                .setUploading(new UploadI18N.Uploading()
                    .setStatus(new UploadI18N.Uploading.Status()
                        .setConnecting(getTranslation("component.upload.uploading.status.connecting"))
                        .setStalled(getTranslation("component.upload.uploading.status.stalled"))
                        .setProcessing(getTranslation("component.upload.uploading.status.processing"))
                        .setHeld(getTranslation("component.upload.uploading.status.held")))
                    .setRemainingTime(new UploadI18N.Uploading.RemainingTime()
                        .setPrefix(getTranslation("component.upload.uploading.remainingTime.prefix"))
                        .setUnknown(getTranslation("component.upload.uploading.remainingTime.unknown")))
                .setError(new UploadI18N.Uploading.Error()
                    .setServerUnavailable(
                        getTranslation("component.upload.uploading.error.serverUnavailable"))
                    .setUnexpectedServerError(
                        getTranslation("component.upload.uploading.error.unexpectedServerError"))
                    .setForbidden(getTranslation("component.upload.uploading.error.forbidden"))))
                .setFile(new UploadI18N.File()
                    .setRetry(getTranslation("component.upload.file.retry"))
                    .setStart(getTranslation("component.upload.file.start"))
                    .setRemove(getTranslation("component.upload.file.remove"))
                );
    }
}
