package ch.pfaditools.accounting.ui.views.security;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class AbstractSecurityView extends VerticalLayout {

    private final VerticalLayout layout = new VerticalLayout();

    public AbstractSecurityView() {
        setupStyles();
        render();
        setupBinder();
    }

    private void setupStyles() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    private Component createLayout() {
        layout.setMaxWidth("400px");

        return layout;
    }

    private void render() {
        removeAll();
        add(createLayout());
        layout.add(createHeader(), createContent());
    }

    protected abstract void setupBinder();

    protected abstract Component createHeader();

    protected abstract Component createContent();
}
