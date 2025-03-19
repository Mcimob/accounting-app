package ch.pfaditools.accounting.ui.views.security;

import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class AbstractSecurityView extends AbstractNarrowView {

    private final VerticalLayout layout = new VerticalLayout();

    public AbstractSecurityView() {
        render();
        setupBinder();
    }

    private Component createLayout() {
        layout.setMaxWidth("400px");

        return layout;
    }

    @Override
    protected void render() {
        super.render();
        add(createLayout());
        layout.add(createHeader(), createContent());
    }

    protected abstract void setupBinder();

    protected abstract Component createHeader();

    protected abstract Component createContent();
}
