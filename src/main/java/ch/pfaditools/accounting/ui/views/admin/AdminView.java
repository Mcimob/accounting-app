package ch.pfaditools.accounting.ui.views.admin;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.logger.HasLogger;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.views.HasNotification;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_ADMIN;

@Route(value = ROUTE_ADMIN, layout = MainLayout.class)
@RolesAllowed(ROLE_ADMIN)
public class AdminView extends VerticalLayout implements HasNotification, HasLogger {

    private final GroupService groupService;

    Grid<GroupEntity> groupGrid = new Grid<>();
    Binder<GroupEntity> groupBinder = new Binder<>();

    TextField nameField = new TextField("Name");
    TextField latestUpdatedUserField = new TextField("Latest updated user");
    TextField createdDateTimeField = new TextField("Created date");
    TextField updatedDateTimeField = new TextField("Updated date");

    public AdminView(GroupService groupService) {
        this.groupService = groupService;
        setupBinder();
        updateGridItems();
        render();
    }

    private Component createGrid() {
        groupGrid.addColumn(GroupEntity::getName).setHeader("Name");
        groupGrid.addColumn(AbstractEntity::getLatestUpdatedUser).setHeader("Latest updated user");
        groupGrid.addColumn(AbstractEntity::getCreatedDateTime).setHeader("Created date");
        groupGrid.addColumn(AbstractEntity::getUpdatedDateTime).setHeader("Updated date");

        groupGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        groupGrid.addSelectionListener(event -> {
            Optional<GroupEntity> group = event.getFirstSelectedItem();
            group.ifPresentOrElse(groupBinder::readBean, () -> groupBinder.readBean(new GroupEntity()));
        });
        return groupGrid;
    }

    private Component createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("500px", 2));

        formLayout.add(nameField, latestUpdatedUserField, createdDateTimeField, updatedDateTimeField);

        return formLayout;
    }

    private Component createButtons() {
        Button saveButton = new Button("Save");
        Button removeButton = new Button("Remove");

        saveButton.addClickListener(this::onSaveButtonClicked);
        removeButton.addClickListener(this::onDeleteButtonClicked);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

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
            logInfo("Validation failed", e);
            return;
        }
        groupToSave.updateCreateModifyFields(SecurityUtils.getAuthenticatedUsername());

        ServiceResponse<GroupEntity> saveResponse = groupService.save(groupToSave);
        if (saveResponse.hasErrorMessages()) {
            saveResponse.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }
        groupBinder.readBean(new GroupEntity());
        updateGridItems();
    }

    private void onDeleteButtonClicked(ClickEvent<Button> clickEvent) {
        Optional<GroupEntity> groupToRemove = groupGrid.getSelectedItems().stream().findFirst();
        if (groupToRemove.isEmpty()) {
            showWarningNotification("No group selected");
            return;
        }
        ServiceResponse<GroupEntity> response = groupService.delete(groupToRemove.get());
        if (response.hasErrorMessages()) {
            response.getErrorMessages().forEach(this::showErrorNotification);
        }

        updateGridItems();
    }

    private void render() {
        removeAll();
        add(new H1("Admin View"));
        add(createGrid());
        add(createForm());
        add(createButtons());
    }

    private void updateGridItems() {
        ServiceResponse<Page<GroupEntity>> response = groupService.fetch(Pageable.unpaged(), new GroupEntityFilter());
        if (response.hasErrorMessages()) {
            response.getErrorMessages().forEach(this::showErrorNotification);
            return;
        }

        response.getEntity().map(Slice::getContent)
                .ifPresent(groupGrid::setItems);
    }

    private void setupBinder() {
        groupBinder.forField(nameField)
                .asRequired()
                .bind(GroupEntity::getName, GroupEntity::setName);
        groupBinder.forField(latestUpdatedUserField)
                .bindReadOnly(AbstractEntity::getLatestUpdatedUser);
        groupBinder.forField(createdDateTimeField)
                .bindReadOnly(AbstractEntity::getCreatedDateTimeString);
        groupBinder.forField(updatedDateTimeField)
                .bindReadOnly(AbstractEntity::getUpdatedDateTimeString);
    }
}
