package ch.pfaditools.accounting.util;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.ui.views.AbstractView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.BiConsumer;

public final class CodeUtil {

    public static final int CODE_LENGTH = 24;

    private CodeUtil() { }

    public static Optional<GroupEntity> onCodeButtonClicked(
            PasswordEncoder passwordEncoder,
            GroupService groupService,
            GroupEntity group,
            BiConsumer<GroupEntity, String> setter,
            String codeText,
            AbstractView view) {
        String code = RandomStringUtils.secure().nextAlphanumeric(CODE_LENGTH);
        String encodedCode = passwordEncoder.encode(code);
        setter.accept(group, encodedCode);
        ServiceResponse<GroupEntity> saveResponse = groupService.save(group);
        Optional<GroupEntity> newGroup = saveResponse.getEntity();
        if (saveResponse.hasErrorMessages() || newGroup.isEmpty()) {
            view.showMessagesFromResponse(saveResponse);
            return Optional.empty();
        }

        Dialog dialog = new Dialog();

        Button closeButton = new Button(view.getTranslation("view.general.close"));
        closeButton.addClickListener(click -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(closeButton);

        dialog.setHeaderTitle(view.getTranslation("view.admin.codeDialogTitle", codeText));
        dialog.add(new Text(view.getTranslation("view.admin.codeDialogText", codeText, code)));

        dialog.open();

        return newGroup;
    }
}
