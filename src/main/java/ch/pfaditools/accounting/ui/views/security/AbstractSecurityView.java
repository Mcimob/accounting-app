package ch.pfaditools.accounting.ui.views.security;

import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import com.vaadin.flow.component.Component;

public abstract class AbstractSecurityView extends AbstractNarrowView {

    public AbstractSecurityView() {
        render();
        setupBinder();
    }

    @Override
    protected void render() {
        super.render();
        add(createHeader(), createContent());
    }

    protected abstract void setupBinder();

    protected abstract Component createHeader();

    protected abstract Component createContent();
}
