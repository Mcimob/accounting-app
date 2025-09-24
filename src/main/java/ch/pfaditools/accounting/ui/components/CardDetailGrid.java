package ch.pfaditools.accounting.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ALIGN_START;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_WIDTH_FULL;

public class CardDetailGrid<T> extends Div {

    private final List<Grid.Column<T>> detailColumns;
    private final Grid.Column<T> cardColumn;

    public CardDetailGrid(Grid<T> grid, List<Grid.Column<T>> detailColumns, Grid.Column<T> cardColumn) {
        this.detailColumns = detailColumns;
        this.cardColumn = cardColumn;
        initStyles();

        add(createToggle(), grid);
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_COLUMN, STYLE_FLEX_ALIGN_START, STYLE_WIDTH_FULL);
    }

    private Component createToggle() {
        IconToggle gridModeToggle = new IconToggle();
        gridModeToggle.setFalseIcon(VaadinIcon.GRID_BIG_O);
        gridModeToggle.setTrueIcon(VaadinIcon.LINES);
        gridModeToggle.addValueChangeListener(e -> {
            detailColumns.forEach(col -> col.setVisible(e.getValue()));
            cardColumn.setVisible(!e.getValue());
        });
        gridModeToggle.setValue(false);
        return gridModeToggle;
    }
}
