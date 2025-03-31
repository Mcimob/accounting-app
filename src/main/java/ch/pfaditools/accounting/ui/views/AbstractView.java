package ch.pfaditools.accounting.ui.views;

import ch.pfaditools.accounting.logger.HasLogger;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_CENTER;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_COLUMN;

public abstract class AbstractView extends Div
        implements HasDynamicTitle, HasLogger, HasNotification, AfterNavigationObserver {

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

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        render();
    }
}
