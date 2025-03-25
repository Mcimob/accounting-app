package ch.pfaditools.accounting.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "payment_entity")
public class PaymentEntity extends AbstractEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<ReceiptEntity> receipts;

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
