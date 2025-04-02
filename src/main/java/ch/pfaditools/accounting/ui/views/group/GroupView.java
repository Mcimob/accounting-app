package ch.pfaditools.accounting.ui.views.group;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import ch.pfaditools.accounting.security.SecurityConstants;
import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.ui.DesignConstants;
import ch.pfaditools.accounting.ui.MainLayout;
import ch.pfaditools.accounting.ui.provider.UserProvider;
import ch.pfaditools.accounting.ui.util.GridUtil;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import ch.pfaditools.accounting.util.CodeUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
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
    private final ConfigurableFilterDataProvider<UserEntity, Void, UserEntityFilter> filterDataProvider;

    private final TextField nameField = new TextField(getTranslation("entity.group.name"));
    private final ComboBox<Currency> currencyField = new ComboBox<>(getTranslation("entity.group.currency"));
    private final Button userCodeButton = new Button();
    private final Button groupAdminCodeButton = new Button();
    private final Button saveButton = new Button();
    private final Grid<UserEntity> personGrid = new Grid<>();

    private final Binder<GroupEntity> binder = new Binder<>();
    private final UserService userService;

    private GroupEntity group;
    private UserEntityFilter userFilter;

    public GroupView(GroupService groupService, UserService userService, PasswordEncoder passwordEncoder) {
        this.groupService = groupService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.filterDataProvider = new UserProvider(userService).withConfigurableFilter();
        setupCurrencyField();
        setupBinder();
        setupGroup();
        setupButtons();
        setupFilter();
    }

    private void setupFilter() {
        this.userFilter = new UserEntityFilter();
        userFilter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
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

    private Component createGrid() {
        Grid.Column<UserEntity> usernameColumn = personGrid.addColumn(UserEntity::getUsername)
                .setHeader(getTranslation("entity.user.username"))
                .setSortable(true);
        personGrid.addColumn(UserEntity::getDisplayName)
                .setHeader(getTranslation("entity.user.displayName"))
                .setSortable(true);
        personGrid.addComponentColumn(this::createHasGroupAdminRoleIndicator)
                .setHeader(getTranslation("view.group.isGroupAdmin"));
        personGrid.addComponentColumn(this::createDeleteButton)
                .setHeader(getTranslation("view.general.delete"));

        GridUtil.addHeaderFilterCell(personGrid, userFilter, filterDataProvider,
        usernameColumn,
                UserEntityFilter::setUsername,
                new TextField());

        personGrid.getColumns().forEach(c -> c.setAutoWidth(true));

        personGrid.setItems(filterDataProvider);

        return personGrid;
    }

    private Component createHasGroupAdminRoleIndicator(UserEntity user) {
        Icon icon;
        if (user.getRoles().contains(SecurityConstants.ROLE_GROUP_ADMIN_STRING)) {
            icon = VaadinIcon.CHECK_CIRCLE.create();
            icon.setColor(DesignConstants.CLR_REGULAR);
        } else {
            icon = VaadinIcon.CLOSE_CIRCLE.create();
            icon.setColor(DesignConstants.CLR_ACCENT);
        }
        return icon;
    }

    private Component createDeleteButton(UserEntity user) {
        Button deleteButton = new Button(VaadinIcon.TRASH.create());
        deleteButton.addClickListener(click -> {
            Dialog dialog = new Dialog(getTranslation("view.general.deleteConfirmation"));
            Button cancelButton = new Button(getTranslation("view.general.cancel"));
            cancelButton.addClickListener(c -> dialog.close());
            Button confirmButton = new Button(getTranslation("view.general.delete"));
            confirmButton.addClickListener(c -> {
                ServiceResponse<UserEntity> response = userService.delete(user);
                showMessagesFromResponse(response);
                if (!response.hasErrorMessages()) {
                    filterDataProvider.refreshAll();
                }
                dialog.close();
            });
            confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            dialog.getFooter().add(cancelButton, confirmButton);
            dialog.add(new Text(getTranslation("view.group.deleteDialog.text")));
            dialog.open();
        });
        return deleteButton;
    }

    @Override
    protected void render() {
        super.render();
        add(new H1(getPageTitle()));
        add(createForm());
        add(createSaveButton());
        add(new H2(getTranslation("view.group.users")));
        add(createGrid());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.group.title");
    }
}
