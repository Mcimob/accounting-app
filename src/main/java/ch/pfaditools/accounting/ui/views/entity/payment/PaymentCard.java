package ch.pfaditools.accounting.ui.views.entity.payment;

import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_LIGHT_REGULAR;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_REGULAR;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_JUSTIFY_END;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FONT_SIZE_S;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FW_500;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_MARGIN_M;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_PADDING_S;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_TEXT_COLOR_WHITE;


public class PaymentCard extends Card {

    private final PaymentEntity payment;

    public PaymentCard(PaymentEntity payment) {
        this.payment = payment;
        createCard();
    }

    private void createCard() {
        removeAll();

        Div title = new Div(payment.getTitle());
        title.addClassNames("text-wrap");
        setTitle(title);

        setSubtitle(new Div(payment.getReceiptsAmount().toString()));

        Div createdTimeDiv = new Div("%s: %s".formatted(
                getTranslation("entity.abstract.createdDateTime"),
                payment.getCreatedDateTimeString(getLocale())));
        createdTimeDiv.addClassNames(STYLE_FONT_SIZE_S);

        Details details = new Details("%s %s".formatted(
                payment.getReceipts().size(), getTranslation("entity.payment.receipts")));
        details.addClassNames(STYLE_BG_LIGHT_REGULAR, STYLE_BORDER_RADIUS_S, STYLE_PADDING_S);
        payment.getReceipts().stream()
                .map(ReceiptEntity::getName)
                .map(Div::new)
                .forEach(div -> {
                    div.addClassNames(STYLE_BG_REGULAR, STYLE_PADDING_S, STYLE_MARGIN_M,
                            STYLE_BORDER_RADIUS_S, STYLE_TEXT_COLOR_WHITE, STYLE_FW_500, "text-wrap");
                    details.add(div);
                });
        add(details);

        Div bottom = new Div();
        bottom.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_JUSTIFY_END);
        bottom.add(createdTimeDiv);

        add(bottom);
    }
}
