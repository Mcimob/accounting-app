package ch.pfaditools.accounting.ui.views;

import ch.pfaditools.accounting.backend.service.BaseService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.logger.HasLogger;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;

import java.util.Optional;

public abstract class AbstractEditEntityView<T extends AbstractEntity, F extends AbstractFilter<T>>
        extends AbstractNarrowView
        implements HasLogger, HasNotification, HasUrlParameter<String>, HasDynamicTitle {

    public static final String KEY_ENTITY = "entityId";

    private final transient BaseService<T, F> service;

    private final Button deleteButton = new Button(getTranslation("view.general.delete"));
    private final Button saveButton = new Button(getTranslation("view.general.save"));

    protected final Binder<T> binder = new Binder<>();

    protected T entity;

    public AbstractEditEntityView(BaseService<T, F> service) {
        this.service = service;
        setupFields();
        setupBinder();
        setupButtons();
    }

    protected final void render() {
        super.render();
        add(createForm());
        add(createButtonsBar());
    }

    private Component createButtonsBar() {
        HorizontalLayout buttonsBar = new HorizontalLayout(deleteButton, saveButton);
        buttonsBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttonsBar.setWidthFull();

        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return buttonsBar;
    }

    private void setupButtons() {
        saveButton.addClickListener(this::onSaveButtonClick);
        deleteButton.addClickListener(this::onDeleteButtonClick);
    }

    private void onSaveButtonClick(ClickEvent<Button> event) {
        try {
            binder.writeBean(entity);
        } catch (ValidationException e) {
            getLogger().warn("Validation failed for saving receipt", e);
            return;
        }
        beforeSave();

        entity.updateCreateModifyFields(SecurityUtils.getAuthenticatedUsername());
        ServiceResponse<T> response = service.save(entity);
        if (response.hasErrorMessages()) {
            showMessagesFromResponse(response);
            return;
        }
        response.getInfoMessages().forEach(this::showSuccessNotification);

        afterSave();
        UI.getCurrent().getPage().getHistory().back();
    }

    private void onDeleteButtonClick(ClickEvent<Button> event) {
        if (entity.getId() == null) {
            UI.getCurrent().getPage().getHistory().back();
            return;
        }

        ServiceResponse<T> receiptResponse = service.delete(entity);
        if (receiptResponse.hasErrorMessages()) {
            showMessagesFromResponse(receiptResponse);
            return;
        }
        afterDelete();
        UI.getCurrent().getPage().getHistory().back();
    }

    protected void afterDelete() { }

    protected void beforeSave() { }

    protected void afterSave() { }

    protected abstract void setupFields();

    protected abstract void setupBinder();

    protected abstract Component createForm();

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        event.getLocation().getQueryParameters().getSingleParameter(KEY_ENTITY).ifPresentOrElse(entityId -> {
            long id;
            try {
                id = Long.parseLong(entityId);
            } catch (NumberFormatException e) {
                getLogger().info("Invalid entity ID: {}", entityId);
                UI.getCurrent().getPage().getHistory().back();
                return;
            }
            ServiceResponse<T> response = service.fetchById(id);
            Optional<T> entityOptional = response.getEntity();
            if (response.hasErrorMessages() || entityOptional.isEmpty()) {
                showMessagesFromResponse(response);
                UI.getCurrent().getPage().getHistory().back();
                return;
            }
            entity = entityOptional.get();
            binder.readBean(entity);
            afterNavigation();
        }, () -> entity = createEntity());
        render();
    }

    protected abstract T createEntity();

    protected void afterNavigation() { }
}
