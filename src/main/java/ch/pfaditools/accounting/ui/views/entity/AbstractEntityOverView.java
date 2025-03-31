package ch.pfaditools.accounting.ui.views.entity;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.ui.provider.AbstractEntityProvider;
import ch.pfaditools.accounting.ui.views.AbstractNarrowView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.QueryParameters;

import java.util.List;
import java.util.function.BiConsumer;

import static ch.pfaditools.accounting.ui.views.entity.AbstractEditEntityView.KEY_ENTITY;

public abstract class AbstractEntityOverView<T extends AbstractEntity, F extends AbstractFilter<T>>
        extends AbstractNarrowView {

    private final ConfigurableFilterDataProvider<T, Void, F> filterDataProvider;
    private final String addingRoute;
    private final String buttonText;

    protected F filter;

    protected final Grid<T> grid = new Grid<>();

    protected AbstractEntityOverView(AbstractEntityProvider<T, F> provider, String addingRoute, String buttonText) {
        this.filterDataProvider = provider.withConfigurableFilter();
        this.addingRoute = addingRoute;
        this.buttonText = buttonText;
        filter = getBaseFilter();
        setupGrid();
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
    }

    private Component createAddButton() {
        Button createButton = new Button(getTranslation(buttonText));
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(click -> UI.getCurrent().navigate(addingRoute));

        return createButton;
    }

    protected void refreshFilter() {
        filterDataProvider.setFilter(filter);
        filterDataProvider.refreshAll();
    }

    protected abstract Component createGrid();

    @Override
    protected void render() {
        super.render();
        add(new H1(getPageTitle()));
        add(createAddButton());
        add(createGrid());
    }

    protected abstract F getBaseFilter();

    protected <V> void addHeaderFilterCell(Grid.Column<T> column, BiConsumer<F, V> filterSetter, HasValue<?, V> field) {
        List<HeaderRow> headers = grid.getHeaderRows();
        HeaderRow header;
        if (headers.isEmpty()) {
            header = grid.appendHeaderRow();
        } else {
            header = headers.getFirst();
        }

        field.addValueChangeListener(event -> {
            filterSetter.accept(filter, event.getValue());
            refreshFilter();
        });
        if (field instanceof HasValueChangeMode hasChangeMode) {
            hasChangeMode.setValueChangeMode(ValueChangeMode.EAGER);
        }
        if (field instanceof HasPlaceholder hasPlaceholder) {
            hasPlaceholder.setPlaceholder(column.getHeaderText());
        }

        if (field instanceof HasSize hasSize) {
            hasSize.setWidthFull();
        }

        if (field instanceof Component component) {
            header.getCell(column).setComponent(component);
        } else {
            throw new IllegalArgumentException("Field must be a component");
        }
    }

}
