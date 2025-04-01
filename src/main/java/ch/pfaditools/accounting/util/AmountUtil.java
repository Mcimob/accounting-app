package ch.pfaditools.accounting.util;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Currency;

public final class AmountUtil {

    public static final int BASE = 10;

    private AmountUtil() { }

    private static final Logger LOGGER = LoggerFactory.getLogger(AmountUtil.class);

    public static String fromAmount(long amount) {
        NumberFormat nf = getCurrencyFormat();
        DecimalFormatSymbols symbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");

        ((DecimalFormat) nf).setDecimalFormatSymbols(symbols);

        return nf.format(getAmountFraction(amount, nf.getCurrency())).trim();
    }

    public static String fromAmountWithCurrency(long amount) {
        NumberFormat nf = getCurrencyFormat();
        return nf.format(getAmountFraction(amount, nf.getCurrency())).trim();
    }

    private static NumberFormat getCurrencyFormat() {
        NumberFormat n = NumberFormat.getCurrencyInstance();
        Currency currency = Currency.getInstance(SecurityUtils.getAuthenticatedUserGroup().getCurrency());
        n.setCurrency(currency);
        return n;
    }

    private static double getAmountFraction(long amount, Currency currency) {
        return amount / getCurrencyRatio(currency);
    }

    public static double getCurrencyRatio(Currency currency) {
        return Math.pow(BASE, currency.getDefaultFractionDigits());
    }

    public static Long fromString(String amount) {
        NumberFormat nf = getCurrencyFormat();
        try {
            return (long) (nf.parse(amount).doubleValue() * getCurrencyRatio(nf.getCurrency()));
        } catch (ParseException e) {
            LOGGER.info("Could not parse amount {}", amount, e);
        }
        return null;
    }

    public static long getAmountSum(Collection<ReceiptEntity> receipts) {
        return receipts.stream()
                .map(ReceiptEntity::getAmount)
                .reduce(0L, Long::sum);
    }
}
