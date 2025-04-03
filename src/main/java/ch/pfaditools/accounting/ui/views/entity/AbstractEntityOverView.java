package ch.pfaditools.accounting.ui.views.entity;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.ui.provider.AbstractEntityProvider;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.QueryParameters;

import static ch.pfaditools.accounting.ui.views.entity.AbstractEditEntityView.KEY_ENTITY;

public abstract class AbstractEntityOverView<T extends AbstractEntity, F extends AbstractFilter<T>>
        extends AbstractNarrowView {

    protected final ConfigurableFilterDataProvider<T, Void, F> filterDataProvider;
    private final String addingRoute;
    private final String buttonText;

    protected F filter;

    protected final Grid<T> grid = new Grid<>();

    protected AbstractEntityOverView(AbstractEntityProvider<T, F> provider, String addingRoute, String buttonText) {
        this.filterDataProvider = provider.withConfigurableFilter();
        this.addingRoute = addingRoute;
        this.buttonText = buttonText;
        setupFilter();
    }

    private void setupFilter() {
        filter = getBaseFilter();
        filterDataProvider.setFilter(filter);
        filterDataProvider.refreshAll();
    }

    private void setupGrid() {
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(receipt ->
                    UI.getCurrent().navigate(
                            addingRoute,
                            QueryParameters.of(KEY_ENTITY, receipt.getId().toString())));
        });
        grid.setItems(filterDataProvider);
        grid.setWidthFull();
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private Component createAddButton() {
        Button createButton = new Button(getTranslation(buttonText));
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(click -> UI.getCurrent().navigate(addingRoute));

        return createButton;
    }

    protected abstract Component createGrid();

    @Override
    protected void render() {
        super.render();
        add(new H1(getPageTitle()));
        add(createAddButton());
        add(createGrid());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        super.afterNavigation(event);
        setupGrid();
    }

    protected abstract F getBaseFilter();

}
