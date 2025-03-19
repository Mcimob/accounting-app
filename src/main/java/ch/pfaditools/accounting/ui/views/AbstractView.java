package ch.pfaditools.accounting.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Style;

public abstract class AbstractView extends Div {

    private final Div layout = new Div();

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
        layout.getStyle().setDisplay(Style.Display.FLEX);
        layout.getStyle().setFlexDirection(Style.FlexDirection.COLUMN);
    }
}
