package ch.pfaditools.accounting.util;

import ch.pfaditools.accounting.model.entity.ReceiptEntity;

import java.util.Collection;

public final class AmountUtil {

    private AmountUtil() { }

    public static final float CENTS_PER_CURRENCY = 100f;

    public static String fromAmount(long amount) {
        return "%.2f".formatted(amount / CENTS_PER_CURRENCY);
    }

    public static long fromString(String amount) {
        return (long) (Float.parseFloat(amount) * CENTS_PER_CURRENCY);
    }

    public static long getAmountSum(Collection<ReceiptEntity> receipts) {
        return receipts.stream()
                .map(ReceiptEntity::getAmount)
                .reduce(0L, Long::sum);
    }
}
