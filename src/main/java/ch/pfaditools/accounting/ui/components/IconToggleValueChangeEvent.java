package ch.pfaditools.accounting.ui.components;

import com.vaadin.flow.component.ComponentEvent;

public class IconToggleValueChangeEvent extends ComponentEvent<IconToggle> {

    private final boolean value;

    public IconToggleValueChangeEvent(IconToggle source, boolean fromClient, boolean value) {
        super(source, fromClient);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
