package ch.pfaditools.accounting.ui.views.entity.payment;

import ch.pfaditools.accounting.model.entity.PaymentEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.QueryParameters;

import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_LIGHT_REGULAR;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BG_REGULAR;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_JUSTIFY_END;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FONT_SIZE_S;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_FW_500;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_MARGIN_M;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_PADDING_S;
import static ch.pfaditools.accounting.ui.DesignConstants.STYLE_TEXT_COLOR_WHITE;
import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_EDIT_RECEIPT;
import static ch.pfaditools.accounting.ui.views.entity.AbstractEditEntityView.KEY_ENTITY;


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
                .map(this::createDetail)
                .forEach(details::add);
        add(details);

        Div bottom = new Div();
        bottom.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_JUSTIFY_END);
        bottom.add(createdTimeDiv);

        add(bottom);
    }

    private Component createDetail(ReceiptEntity receipt) {
        Div nameDiv = new Div(receipt.getName());
        nameDiv.addClassNames("text-wrap", STYLE_FW_500);


        Icon icon = VaadinIcon.EXTERNAL_LINK.create();
        icon.setSize("2em");
        Button link = new Button(icon);
        link.addClickListener(click ->
                UI.getCurrent().navigate(
                    ROUTE_EDIT_RECEIPT,
                    QueryParameters.of(KEY_ENTITY, receipt.getId().toString())));

        Div div = new Div(nameDiv, link);
        div.addClassNames(STYLE_BG_REGULAR, STYLE_PADDING_S, STYLE_MARGIN_M,
                STYLE_BORDER_RADIUS_S, STYLE_TEXT_COLOR_WHITE,
                STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN, STYLE_FLEX_ALIGN_CENTER);
        return div;
    }
}
