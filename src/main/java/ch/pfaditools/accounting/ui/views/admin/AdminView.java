package ch.pfaditools.accounting.ui.views.admin;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.logger.HasLogger;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.views.AbstractWideView;
import ch.pfaditools.accounting.ui.views.HasNotification;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.function.BiConsumer;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_ADMIN;

@Route(value = ROUTE_ADMIN, layout = MainLayout.class)
@RolesAllowed(ROLE_ADMIN)
public class AdminView extends AbstractWideView implements HasNotification, HasLogger, HasDynamicTitle {

    public static final int CODE_LENGTH = 24;
    private final GroupService groupService;
    private final GroupProvider groupProvider;
    private final PasswordEncoder passwordEncoder;

    private final Grid<GroupEntity> groupGrid = new Grid<>();
    private final Binder<GroupEntity> groupBinder = new Binder<>();

    private final TextField nameField = new TextField();
    private final TextField latestUpdatedUserField = new TextField();
    private final TextField createdDateTimeField = new TextField();
    private final TextField updatedDateTimeField = new TextField();

    private final Button removeButton = new Button();

    public AdminView(GroupService groupService, GroupProvider groupProvider, PasswordEncoder passwordEncoder) {
        this.groupService = groupService;
        this.groupProvider = groupProvider;
        this.passwordEncoder = passwordEncoder;
        setupBinder();
        render();
    }

    private Component createGrid() {
        groupGrid.addColumn(GroupEntity::getName)
                .setHeader(getTranslation("entity.group.name"));
        groupGrid.addColumn(AbstractEntity::getLatestUpdatedUser)
                .setHeader(getTranslation("entity.abstract.lastUpdatedUser"));
        groupGrid.addColumn(AbstractEntity::getCreatedDateTime)
                .setHeader(getTranslation("entity.abstract.createdDateTime"));
        groupGrid.addColumn(AbstractEntity::getUpdatedDateTime)
                .setHeader(getTranslation("entity.abstract.updatedDateTime"));
        groupGrid.addComponentColumn(this::createUserCodeButton)
                .setHeader(getTranslation("entity.group.userCode"));
        groupGrid.addComponentColumn(this::createGroupAdminCodeButton)
                .setHeader(getTranslation("entity.group.groupAdminCode"));

        groupGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        groupGrid.addSelectionListener(event -> {
            Optional<GroupEntity> group = event.getFirstSelectedItem();
            group.ifPresentOrElse(groupBinder::readBean, () -> groupBinder.readBean(new GroupEntity()));
            removeButton.setEnabled(group.isPresent());
        });
        groupGrid.setItems(groupProvider);
        return groupGrid;
    }

    private Component createUserCodeButton(GroupEntity group) {
        Button button = new Button();
        button.setIcon(VaadinIcon.PLUS.create());
        button.addThemeVariants(ButtonVariant.LUMO_ICON);
        button.addClickListener(event -> onAddCodeButtonClicked(
                group,
                GroupEntity::setGroupCode,
                getTranslation("entity.group.userCode")));
        return button;
    }

    private Component createGroupAdminCodeButton(GroupEntity group) {
        Button button = new Button();
        button.setIcon(VaadinIcon.PLUS.create());
        button.addThemeVariants(ButtonVariant.LUMO_ICON);
        button.addClickListener(event -> onAddCodeButtonClicked(
                group,
                GroupEntity::setGroupAdminCode,
                getTranslation("entity.group.groupAdminCode")));
        return button;
    }

    private void onAddCodeButtonClicked(
            GroupEntity group, BiConsumer<GroupEntity, String> setter, String codeText) {
        String code = RandomStringUtils.secure().nextAlphanumeric(CODE_LENGTH);
        String encodedCode = passwordEncoder.encode(code);
        setter.accept(group, encodedCode);
        ServiceResponse<GroupEntity> saveResponse = groupService.save(group);
        if (saveResponse.hasErrorMessages()) {
            saveResponse.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }

        groupProvider.refreshAll();

        Dialog dialog = new Dialog();

        Button closeButton = new Button(getTranslation("view.general.close"));
        closeButton.addClickListener(click -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(closeButton);

        dialog.setHeaderTitle(getTranslation("view.admin.codeDialogTitle", codeText));
        dialog.add(new Text(getTranslation("view.admin.codeDialogText", codeText, code)));

        dialog.open();
    }

    private Component createForm() {
        nameField.setLabel(getTranslation("entity.group.name"));
        latestUpdatedUserField.setLabel(getTranslation(getTranslation("entity.abstract.lastUpdatedUser")));
        createdDateTimeField.setLabel(getTranslation("entity.abstract.createdDateTime"));
        updatedDateTimeField.setLabel(getTranslation("entity.abstract.updatedDateTime"));

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 2));

        formLayout.add(nameField, latestUpdatedUserField, createdDateTimeField, updatedDateTimeField);

        return formLayout;
    }

    private Component createButtons() {
        Button saveButton = new Button(getTranslation("view.general.save"));
        removeButton.setText(getTranslation("view.general.delete"));

        saveButton.addClickListener(this::onSaveButtonClicked);
        removeButton.addClickListener(this::onDeleteButtonClicked);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        removeButton.setEnabled(false);

        return new HorizontalLayout(removeButton, saveButton);
    }

    private void onSaveButtonClicked(ClickEvent<Button> clickEvent) {
        GroupEntity groupToSave;
        if (groupGrid.getSelectedItems().isEmpty()) {
            groupToSave = new GroupEntity();
        } else {
            groupToSave = groupGrid.getSelectedItems().iterator().next();
        }

        try {
            groupBinder.writeBean(groupToSave);
        } catch (ValidationException e) {
            getLogger().info("Validation failed", e);
            return;
        }
        groupToSave.updateCreateModifyFields(SecurityUtils.getAuthenticatedUsername());

        ServiceResponse<GroupEntity> saveResponse = groupService.save(groupToSave);
        if (saveResponse.hasErrorMessages()) {
            saveResponse.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }
        groupBinder.readBean(new GroupEntity());
        groupProvider.refreshAll();
    }

    private void onDeleteButtonClicked(ClickEvent<Button> clickEvent) {
        Optional<GroupEntity> groupToRemove = groupGrid.getSelectedItems().stream().findFirst();
        if (groupToRemove.isEmpty()) {
            showWarningNotification(getTranslation("view.admin.notification.noGroupSelected"));
            return;
        }
        ServiceResponse<GroupEntity> response = groupService.delete(groupToRemove.get());
        if (response.hasErrorMessages()) {
            response.getErrorMessages().forEach(this::showErrorNotification);
        }

        groupProvider.refreshAll();
    }

    @Override
    protected void render() {
        super.render();
        add(new H1(getTranslation("view.admin.title")));
        add(createGrid());
        add(createForm());
        add(createButtons());
    }

    private void setupBinder() {
        groupBinder.forField(nameField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.group.name")))
                .bind(GroupEntity::getName, GroupEntity::setName);
        groupBinder.forField(latestUpdatedUserField)
                .bindReadOnly(AbstractEntity::getLatestUpdatedUser);
        groupBinder.forField(createdDateTimeField)
                .bindReadOnly(AbstractEntity::getCreatedDateTimeString);
        groupBinder.forField(updatedDateTimeField)
                .bindReadOnly(AbstractEntity::getUpdatedDateTimeString);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.admin.title");
    }
}
