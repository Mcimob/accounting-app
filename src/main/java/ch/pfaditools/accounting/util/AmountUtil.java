package ch.pfaditools.accounting.util;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.util.Collection;
import java.util.Currency;

public final class AmountUtil {

    public static final int BASE = 10;

    private AmountUtil() { }

    public static double getCurrencyRatio(Currency currency) {
        return Math.pow(BASE, currency.getDefaultFractionDigits());
    }

    public static MonetaryAmount getAmountSum(Collection<ReceiptEntity> receipts) {
        return receipts.stream()
                .map(ReceiptEntity::getAmount)
                .reduce(MonetaryAmount::add)
                .orElse(Money.of(0, SecurityUtils.getGroupCurrencyString()));
    }
}
