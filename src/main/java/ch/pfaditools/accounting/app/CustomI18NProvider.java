package ch.pfaditools.accounting.app;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class CustomI18NProvider implements I18NProvider {

    private static final Locale DEFAULT_LOCALE = Locale.GERMAN;
    private static final List<Locale> SUPPORTED_LOCALES = List.of(DEFAULT_LOCALE);

    @Override
    public List<Locale> getProvidedLocales() {
        return SUPPORTED_LOCALES;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (!SUPPORTED_LOCALES.contains(locale)) {
            locale = DEFAULT_LOCALE;
        }
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", locale);
        String message = bundle.containsKey(key) ? bundle.getString(key) : key;
        return MessageFormat.format(message, params);
    }
}
