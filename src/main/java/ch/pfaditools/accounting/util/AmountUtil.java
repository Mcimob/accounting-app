package ch.pfaditools.accounting.util;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import org.javamoney.moneta.FastMoney;

import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import java.util.Collection;
import java.util.Currency;

public final class AmountUtil {

    public static final int BASE = 10;

    private AmountUtil() { }

    public static double getCurrencyRatio(Currency currency) {
        return Math.pow(BASE, currency.getDefaultFractionDigits());
    }

    public static MonetaryAmount getAmountSum(Collection<ReceiptEntity> receipts) {
        double amount = receipts.stream()
                .map(ReceiptEntity::getAmount)
                .map(MonetaryAmount::getNumber)
                .map(NumberValue::doubleValueExact)
                .reduce(0D, Double::sum);
        return FastMoney.of(amount, SecurityUtils.getGroupCurrencyString());
    }
}
