package ch.pfaditools.accounting.ui.views.entity.receipt;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.ui.DesignConstants;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_ACCENT;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_REGULAR;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BORDER_RADIUS_M;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FONT_SIZE_S;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FW_700;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_GAP_M;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_PADDING_M;

public class ReceiptCard extends Div {

    private final ReceiptEntity receipt;

    public ReceiptCard(ReceiptEntity receipt) {
        this.receipt = receipt;
        createCard();
    }

    private void createCard() {
        removeAll();

        Div titleDiv = new Div(receipt.getName());
        titleDiv.addClassNames(STYLE_FW_700, "text-wrap");
        Div amountDiv = new Div(receipt.getAmount().toString());
        amountDiv.addClassNames(STYLE_FW_700);

        Div top = new Div(titleDiv, amountDiv);
        top.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN);

        Div createdTimeDiv = new Div("%s: %s".formatted(
                getTranslation("entity.abstract.createdDateTime"),
                receipt.getCreatedDateTimeString(getLocale())));
        createdTimeDiv.addClassNames(STYLE_FONT_SIZE_S);

        Div bottom = new Div();
        bottom.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN);
        bottom.add(createIcon(receipt.getPayment() != null));
        bottom.add(createdTimeDiv);

        add(top, bottom);

        addClassNames(STYLE_PADDING_M, STYLE_FLEX_COLUMN, STYLE_GAP_M, STYLE_BORDER_RADIUS_M);

        addClassName(receipt.getPayment() != null
                ? STYLE_BG_REGULAR
                : STYLE_BG_ACCENT);
    }

    private Icon createIcon(Boolean paid) {
        Icon paidIcon = paid
                ? VaadinIcon.CHECK_CIRCLE.create()
                : VaadinIcon.CLOSE_CIRCLE.create();
        paidIcon.setColor(paid
                ? DesignConstants.CLR_REGULAR
                : DesignConstants.CLR_ACCENT);
        paidIcon.setTooltipText(getTranslation(paid
                ? "entity.receipt.paid.true"
                : "entity.receipt.paid.false"));
        return paidIcon;
    }
}
