package ch.pfaditools.accounting.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class AbstractView extends Div {

    private final VerticalLayout layout = new VerticalLayout();

    public AbstractView() {
        addClassName("view");
    }

    @Override
    public void add(Component... components) {
        layout.add(components);
    }

    @Override
    public void add(String text) {
        layout.add(new Text(text));
    }

    protected void render() {
        removeAll();
        super.add(layout);
    }
}
