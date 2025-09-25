package ch.pfaditools.accounting.ui.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public class ConfirmDeleteDialog extends ConfirmDialog {

    public ConfirmDeleteDialog() {
        setHeader(getTranslation("view.general.deleteConfirmation"));

        setCancelable(true);
        setCancelText(getTranslation("view.general.cancel"));

        Button confirmButton = new Button(getTranslation("view.general.delete"));
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        setConfirmButton(confirmButton);
    }
}
