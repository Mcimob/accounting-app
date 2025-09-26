package ch.pfaditools.accounting.ui.views.entity.receipt;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.ui.DesignConstants;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_LIGHT_ACCENT;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_LIGHT_REGULAR;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FONT_SIZE_S;

public class ReceiptCard extends Card {

    private final ReceiptEntity receipt;

    public ReceiptCard(ReceiptEntity receipt) {
        this.receipt = receipt;
        createCard();
    }

    private void createCard() {
        removeAll();

        Div titleDiv = new Div(receipt.getName());
        titleDiv.addClassNames("text-wrap");
        setTitle(titleDiv);

        setSubtitle(new Div(receipt.getAmount().toString()));

        Div createdTimeDiv = new Div("%s: %s".formatted(
                getTranslation("entity.abstract.createdDateTime"),
                receipt.getCreatedDateTimeString(getLocale())));
        createdTimeDiv.addClassNames(STYLE_FONT_SIZE_S);

        Div bottom = new Div();
        bottom.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN);
        bottom.add(createIcon(receipt.getPayment() != null));
        bottom.add(createdTimeDiv);

        add(bottom);

        addClassName(receipt.getPayment() != null
                ? STYLE_BG_LIGHT_REGULAR
                : STYLE_BG_LIGHT_ACCENT);
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
