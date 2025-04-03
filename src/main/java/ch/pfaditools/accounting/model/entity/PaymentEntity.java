package ch.pfaditools.accounting.model.entity;

import ch.pfaditools.accounting.model.converter.MoneyConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Formula;

import javax.money.MonetaryAmount;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "payment_entity")
public class PaymentEntity extends AbstractEntity {

    public PaymentEntity() { }

    public PaymentEntity(PaymentEntity paymentEntity) {
        super(paymentEntity);
        this.title = paymentEntity.getTitle();
        this.description = paymentEntity.getDescription();
        this.receipts = paymentEntity.getReceipts();
    }

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payment")
    private Set<ReceiptEntity> receipts = new HashSet<>();

    @Formula("(COALESCE((SELECT SUM(re.amount) FROM receipt_entity re WHERE re.payment_id = id), 0))")
    @Convert(converter = MoneyConverter.class)
    private MonetaryAmount receiptsAmount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ReceiptEntity> getReceipts() {
        return receipts;
    }

    public MonetaryAmount getReceiptsAmount() {
        return receiptsAmount;
    }

    public void setReceipts(Set<ReceiptEntity> receipts) {
        this.receipts = receipts;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PaymentEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(receipts, that.receipts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                title,
                description,
                receipts);
    }

    @Override
    public String toString() {
        return "PaymentEntity{"
                + "title='" + title + '\''
                + ", description='" + description + '\''
                + ", receipts=" + receipts
                + "} " + super.toString();
    }
}
