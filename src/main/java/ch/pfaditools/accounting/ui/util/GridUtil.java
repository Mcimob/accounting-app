package ch.pfaditools.accounting.ui.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.List;
import java.util.function.BiConsumer;

public final class GridUtil {

    private GridUtil() { }

    public static <V, T, F> void addHeaderFilterCell(
            Grid<T> grid,
            F filter,
            ConfigurableFilterDataProvider<T, Void, F> filterDataProvider,
            Grid.Column<T> column,
            BiConsumer<F, V> filterSetter,
            HasValue<?, V> field) {
        List<HeaderRow> headers = grid.getHeaderRows();
        HeaderRow header;
        if (headers.size() == 1) {
            header = grid.appendHeaderRow();
        } else {
            header = headers.getLast();
        }

        field.addValueChangeListener(event -> {
            filterSetter.accept(filter, event.getValue());
            filterDataProvider.setFilter(filter);
            filterDataProvider.refreshAll();
        });

        if (field instanceof HasValueChangeMode hasChangeMode) {
            hasChangeMode.setValueChangeMode(ValueChangeMode.EAGER);
        }
        if (field instanceof HasPlaceholder hasPlaceholder) {
            hasPlaceholder.setPlaceholder(grid.getHeaderRows().getFirst().getCell(column).getText());
        }

        if (field instanceof Component component) {
            header.getCell(column).setComponent(component);
        } else {
            throw new IllegalArgumentException("Field must be a component");
        }
    }
}
