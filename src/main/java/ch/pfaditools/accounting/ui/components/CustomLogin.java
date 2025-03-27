package ch.pfaditools.accounting.ui.components;

import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;

public class CustomLogin extends LoginForm {

    public CustomLogin() {
        setI18n(translate());
    }

    private LoginI18n translate() {
        LoginI18n i18n = new LoginI18n();

        LoginI18n.Form form = new LoginI18n.Form();
        form.setPassword(getTranslation("entity.user.password"));
        form.setUsername(getTranslation("entity.user.username"));
        form.setTitle(getTranslation("component.login.title"));
        form.setSubmit(getTranslation("component.login.submit"));
        form.setForgotPassword(getTranslation("component.login.forgotPassword"));

        LoginI18n.ErrorMessage error = new LoginI18n.ErrorMessage();
        error.setTitle(getTranslation("component.login.error.title"));
        error.setMessage(getTranslation("component.login.error.message"));

        i18n.setForm(form);
        i18n.setErrorMessage(error);

        return i18n;
    }
}
