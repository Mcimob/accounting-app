package ch.pfaditools.accounting.ui.views.security.register;

import ch.pfaditools.accounting.backend.service.GroupService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.GroupEntityFilter;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.regex.Pattern;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_GROUP_ADMIN_STRING;
import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_USER_STRING;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_CONTENT_MATCH_WIDTH;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_WIDTH_NARROW;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_LOGIN;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_REGISTER;

@Route(ROUTE_REGISTER)
@AnonymousAllowed
public class RegisterView extends AbstractNarrowView {

    private static final String ONE_DIGIT = ".*[0-9].*";
    private static final String LOWER_CASE = ".*[a-z].*";
    private static final String UPPER_CASE = ".*[A-Z].*";
    private static final String SPECIAL_CHAR = ".*[@#$%^&+=].*";
    private static final String NO_SPACE = "\\S+$";
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 20;

    private final UserService userService;
    private final GroupService groupService;
    private final PasswordEncoder passwordEncoder;

    private Binder<UserWithCodeAndGroup> binder;

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();
    private final TextField groupField = new TextField();
    private final PasswordField codeField = new PasswordField();

    private final UserWithCodeAndGroup userWithCodeAndGroup = new UserWithCodeAndGroup();

    private Binder.Binding<UserWithCodeAndGroup, String> passwordBinding;

    public RegisterView(UserService userService, GroupService groupService, PasswordEncoder passwordEncoder) {
        super();
        this.userService = userService;
        this.groupService = groupService;
        this.passwordEncoder = passwordEncoder;
        setupBinder();
        setupPasswordField();
    }

    private void setupBinder() {
        binder = new Binder<>();
        binder.forField(usernameField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.user.username")))
                .bind(u -> u.getUser().getUsername(), (u, val) -> u.getUser().setUsername(val));
        passwordBinding = binder.forField(passwordField)
                .withValidator(pass -> pass.length() >= MIN_PASSWORD_LENGTH && pass.length() <= MAX_PASSWORD_LENGTH,
                        getTranslation("view.register.error.password.length"))
                .withValidator(pass -> Pattern.compile(ONE_DIGIT).matcher(pass).matches(),
                        getTranslation("view.register.error.password.oneDigit"))
                .withValidator(pass -> Pattern.compile(LOWER_CASE).matcher(pass).matches(),
                        getTranslation("view.register.error.password.lowerCase"))
                .withValidator(pass -> Pattern.compile(UPPER_CASE).matcher(pass).matches(),
                        getTranslation("view.register.error.password.upperCase"))
                .withValidator(pass -> Pattern.compile(SPECIAL_CHAR).matcher(pass).matches(),
                        getTranslation("view.register.error.password.specialChar"))
                .withValidator(pass -> Pattern.compile(NO_SPACE).matcher(pass).matches(),
                        getTranslation("view.register.error.password.noSpace"))
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.user.password")))
                .bind(u -> u.getUser().getPassword(), (u, val) -> u.getUser().setPassword(val));
        binder.forField(confirmPasswordField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.user.password")))
                .withValidator(val -> val.equals(passwordField.getValue()),
                        getTranslation("view.register.error.password.equal"))
                .bind(u -> u.getUser().getPassword(), (u, val) -> u.getUser().setPassword(val));
        binder.forField(groupField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.user.group")))
                .bind(UserWithCodeAndGroup::getGroupName, UserWithCodeAndGroup::setGroupName);
        binder.forField(codeField)
                .asRequired(getTranslation("view.general.error.notEmpty", getTranslation("entity.user.code")))
                .bind(UserWithCodeAndGroup::getCode, UserWithCodeAndGroup::setCode);

        binder.setBean(userWithCodeAndGroup);
    }

    private void setupPasswordField() {
        passwordField.addValueChangeListener(event -> {
           passwordBinding.validate();
        });
        passwordField.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private void registerUser(ClickEvent<Button> clickEvent) {
        try {
            binder.writeBean(userWithCodeAndGroup);
        } catch (ValidationException e) {
            getLogger().info("Validation failed for some fields", e);
            return;
        }

        GroupEntityFilter groupFilter = new GroupEntityFilter();
        groupFilter.setName(userWithCodeAndGroup.getGroupName());
        ServiceResponse<GroupEntity> groupResponse = groupService.fetchOne(groupFilter);
        Optional<GroupEntity> group = groupResponse.getEntity();
        if (groupResponse.hasErrorMessages() || group.isEmpty()) {
            showMessagesFromResponse(groupResponse);
            return;
        }

        UserEntity newUser = userWithCodeAndGroup.getUser();

        boolean matchesGroupAdminCode = passwordEncoder.matches(
                userWithCodeAndGroup.getCode(), group.get().getGroupAdminCode());
        boolean matchesGroupUserCode = passwordEncoder.matches(
                userWithCodeAndGroup.getCode(), group.get().getGroupCode());

        if (matchesGroupAdminCode) {
            newUser.getRoles().add(ROLE_GROUP_ADMIN_STRING);
        } else if (matchesGroupUserCode) {
            newUser.getRoles().add(ROLE_USER_STRING);
        } else {
            showWarningNotification("view.register.notification.invalidCode");
            return;
        }
        newUser.setGroup(group.get());

        UserEntityFilter userFilter = new UserEntityFilter();
        userFilter.setUsername(newUser.getUsername());
        ServiceResponse<UserEntity> userResponse = userService.fetchOne(userFilter);
        if (userResponse.hasErrorMessages()) {
            showMessagesFromResponse(userResponse);
            return;
        }
        if (userResponse.getEntity().isPresent()) {
            showWarningNotification("view.register.notification.userExists");
            return;
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.updateCreateModifyFields("REGISTER");
        ServiceResponse<UserEntity> saveResponse = userService.save(newUser);
        if (saveResponse.hasErrorMessages()) {
            showMessagesFromResponse(saveResponse);
        } else {
            showSuccessNotification("view.register.notification.success");
            UI.getCurrent().navigate(ROUTE_LOGIN);
        }
    }

    private Component createHeader() {
        return new H1(getTranslation("view.register.title"));
    }

    private Component createContent() {
        usernameField.setLabel(getTranslation("entity.user.username"));
        passwordField.setLabel(getTranslation("entity.user.password"));
        confirmPasswordField.setLabel(getTranslation("view.register.repeat", getTranslation("entity.user.password")));
        groupField.setLabel(getTranslation("entity.user.group"));
        codeField.setLabel(getTranslation("entity.user.code"));

        Button registerButton = new Button(getTranslation("view.register.registerButton"), this::registerUser);
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout layout = new VerticalLayout(
                usernameField,
                passwordField,
                confirmPasswordField,
                groupField,
                codeField,
                registerButton);
        layout.addClassNames(STYLE_CONTENT_MATCH_WIDTH, STYLE_WIDTH_NARROW);
        return layout;
    }

    @Override
    protected void render() {
        super.render();
        add(createHeader(), createContent());
    }

    @Override
    public String getPageTitle() {
        return getTranslation("view.register.title");
    }
}
