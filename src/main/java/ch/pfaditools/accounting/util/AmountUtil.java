package ch.pfaditools.accounting.util;

public final class AmountUtil {

    private AmountUtil() { }

    public static final float CENTS_PER_CURRENCY = 100f;

    public static String fromAmount(double amount) {
        return "%.2f".formatted(amount / CENTS_PER_CURRENCY);
    }

    public static double fromString(String amount) {
        return (Float.parseFloat(amount) * CENTS_PER_CURRENCY);
    }
}
