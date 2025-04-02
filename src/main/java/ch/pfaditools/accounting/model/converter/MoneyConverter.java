package ch.pfaditools.accounting.model.converter;

import ch.pfaditools.accounting.security.SecurityUtils;
import ch.pfaditools.accounting.util.AmountUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import org.javamoney.moneta.FastMoney;

import javax.money.MonetaryAmount;
import java.util.Currency;

@Convert
public class MoneyConverter implements AttributeConverter<MonetaryAmount, Long> {
    @Override
    public Long convertToDatabaseColumn(MonetaryAmount attribute) {
        Currency currency = Currency.getInstance(SecurityUtils.getAuthenticatedUserGroup().getCurrency());
        return (long) (AmountUtil.getCurrencyRatio(currency)
                * attribute.getNumber().numberValue(Long.class));
    }

    @Override
    public MonetaryAmount convertToEntityAttribute(Long dbData) {
        Currency currency = Currency.getInstance(SecurityUtils.getAuthenticatedUserGroup().getCurrency());
        return FastMoney.of(dbData
                / AmountUtil.getCurrencyRatio(currency), currency.getCurrencyCode());
    }
}
