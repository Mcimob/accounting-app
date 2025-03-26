package ch.pfaditools.accounting.ui.views;

import static ch.pfaditools.accounting.ui.DesignConstants.VIEW;
import static ch.pfaditools.accounting.ui.DesignConstants.VIEW_WIDE;

public abstract class AbstractWideView extends AbstractView {

    public AbstractWideView() {
        addClassNames(VIEW, VIEW_WIDE);
    }
}
