package ch.pfaditools.accounting.ui.components;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_DARK;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_LIGHT_REGULAR;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_TRANSITION;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BORDER_RADIUS_ROUND;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_PADDING_S;

@Tag("icon-toggle")
public class IconToggle extends Div {

    private final Button falseButton = new Button();
    private final Button trueButton = new Button();

    public IconToggle() {
        initStyles();
        initListeners();
        add(falseButton, trueButton);
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_ROW, STYLE_BG_DARK, STYLE_BORDER_RADIUS_ROUND);
        falseButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        trueButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        falseButton.addClassNames(STYLE_BORDER_RADIUS_ROUND, STYLE_PADDING_S, STYLE_BG_TRANSITION,
                STYLE_BG_LIGHT_REGULAR);
        trueButton.addClassNames(STYLE_BORDER_RADIUS_ROUND, STYLE_PADDING_S, STYLE_BG_TRANSITION);
    }

    private void initListeners() {
        falseButton.addClickListener(e -> {
            setActive(falseButton);
            fireEvent(new IconToggleValueChangeEvent(this, true, false));
        });
        trueButton.addClickListener(e -> {
            setActive(trueButton);
            fireEvent(new IconToggleValueChangeEvent(this, true, true));
        });
    }

    private void setActive(Button active) {
        falseButton.removeClassName(STYLE_BG_LIGHT_REGULAR);
        trueButton.removeClassName(STYLE_BG_LIGHT_REGULAR);
        active.addClassName(STYLE_BG_LIGHT_REGULAR);
    }

    public void setFalseIcon(VaadinIcon icon) {
        falseButton.setIcon(icon.create());
    }

    public void setTrueIcon(VaadinIcon icon) {
        trueButton.setIcon(icon.create());
    }

    public Registration addValueChangeListener(ComponentEventListener<IconToggleValueChangeEvent> listener) {
        return addListener(IconToggleValueChangeEvent.class, listener);
    }

    public void setValue(boolean value) {
        (value
            ? trueButton
            : falseButton)
            .click();
    }
}
