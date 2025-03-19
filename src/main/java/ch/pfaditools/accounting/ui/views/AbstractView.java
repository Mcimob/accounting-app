package ch.pfaditools.accounting.ui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_CENTER;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_COLUMN;

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
        layout.addClassNames(STYLE_FLEX_COLUMN, STYLE_FLEX_CENTER);
    }
}
