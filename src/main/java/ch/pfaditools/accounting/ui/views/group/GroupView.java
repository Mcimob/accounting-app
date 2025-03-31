package ch.pfaditools.accounting.ui.views.group;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import ch.pfaditools.accounting.util.CodeUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Currency;
import java.util.Optional;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_GROUP;

@Route(value = ROUTE_GROUP, layout = MainLayout.class)
@RolesAllowed(ROLE_GROUP_ADMIN)
public class GroupView extends AbstractNarrowView {

    private final GroupService groupService;
    private final PasswordEncoder passwordEncoder;

    private final TextField nameField = new TextField(getTranslation("entity.group.name"));
    private final ComboBox<Currency> currencyField = new ComboBox<>(getTranslation("entity.group.currency"));
    private final Button userCodeButton = new Button();
    private final Button groupAdminCodeButton = new Button();
    private final Button saveButton = new Button();

    private final Binder<GroupEntity> binder = new Binder<>();

    private GroupEntity group;

    public GroupView(GroupService groupService, PasswordEncoder passwordEncoder) {
        this.groupService = groupService;
        this.passwordEncoder = passwordEncoder;
        setupCurrencyField();
        setupBinder();
        setupGroup();
        setupButtons();
    }

    private void setupGroup() {
        this.group = SecurityUtils.getAuthenticatedUserGroup();
        binder.readBean(group);
    }

    private void setupBinder() {
        binder.forField(nameField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.group.name")))
                .bind(GroupEntity::getName, GroupEntity::setName);
        binder.forField(currencyField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.group.currency")))
                .bind(
                        g ->  Currency.getInstance(g.getCurrency()),
                        (g, val) -> g.setCurrency(val.getCurrencyCode()));
    }

    private void setupCurrencyField() {
        currencyField.setItems(Currency.getAvailableCurrencies());
        currencyField.setItemLabelGenerator(curr -> "%s (%s)".formatted(
                curr.getDisplayName(getLocale()), curr.getSymbol(getLocale())));
    }

    private void setupButtons() {
        userCodeButton.setText(getTranslation("entity.group.userCode"));
        userCodeButton.setIcon(VaadinIcon.PLUS.create());
        userCodeButton.addClickListener(click -> {
            Optional<GroupEntity> newGroup = CodeUtil.onCodeButtonClicked(passwordEncoder,
                    groupService,
                    group,
                    GroupEntity::setGroupCode,
                    getTranslation("entity.group.userCode"),
                    this);
            newGroup.ifPresent(g -> this.group = g);
        });

        groupAdminCodeButton.setText(getTranslation("entity.group.groupAdminCode"));
        groupAdminCodeButton.setIcon(VaadinIcon.PLUS.create());
        groupAdminCodeButton.addClickListener(click -> {
            Optional<GroupEntity> newGroup = CodeUtil.onCodeButtonClicked(passwordEncoder,
                    groupService,
                    group,
                    GroupEntity::setGroupAdminCode,
                    getTranslation("entity.group.groupAdminCode"),
                    this);
            newGroup.ifPresent(g ->
                    this.group = g);
        });
    }

    private Component createForm() {
        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        formLayout.add(nameField, currencyField, userCodeButton, groupAdminCodeButton);

        return formLayout;
    }

    private Component createSaveButton() {
        saveButton.setText(getTranslation("view.general.save"));
        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(click -> {
           try {
               binder.writeBean(group);
           } catch (ValidationException e) {
               getLogger().info("Validation failed in GroupView", e);
               return;
           }
            ServiceResponse<GroupEntity> response = groupService.save(group);
            Optional<GroupEntity> newGroup = response.getEntity();
           if (response.hasErrorMessages() || newGroup.isEmpty()) {
               showMessagesFromResponse(response);
               return;
           }
           this.group = newGroup.get();
           showSuccessNotification("view.general.notification.success.save");
        });

        return saveButton;
    }

    @Override
    protected void render() {
        super.render();
        add(new H1(getPageTitle()));
        add(createForm());
        add(createSaveButton());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.group.title");
    }
}
