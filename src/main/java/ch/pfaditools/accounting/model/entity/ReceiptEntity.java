package ch.pfaditools.accounting.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "receipt_entity")
public class ReceiptEntity extends AbstractEntity {

    public static final float CENTS_PER_CURRENCY = 100f;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    private LocalDateTime paidOutAt;

    @OneToOne(fetch = FetchType.EAGER)
    private FileEntity file;

    @ManyToOne(optional = false)
    private GroupEntity group;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountString() {
        return "%.2f".formatted(amount / CENTS_PER_CURRENCY);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setAmountString(String amount) {
        this.amount = (Float.parseFloat(amount) * CENTS_PER_CURRENCY);
    }

    public LocalDateTime getPaidOutAt() {
        return paidOutAt;
    }

    public void setPaidOutAt(LocalDateTime paidOutAt) {
        this.paidOutAt = paidOutAt;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ReceiptEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Double.compare(amount, that.amount) == 0
                && Objects.equals(name, that.name)
                && Objects.equals(description, that.description)
                && Objects.equals(paidOutAt, that.paidOutAt)
                && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                name,
                description,
                amount,
                paidOutAt,
                file);
    }

    @Override
    public String toString() {
        return "ReceiptEntity{"
                + "name='" + name + '\''
                + ", description='" + description + '\''
                + ", amount=" + amount
                + ", paidOutAt=" + paidOutAt
                + ", file=" + file
                + "} " + super.toString();
    }
}
