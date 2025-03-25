package ch.pfaditools.accounting.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "receipt_entity")
public class ReceiptEntity extends AbstractEntity {

    public ReceiptEntity() { }

    public ReceiptEntity(ReceiptEntity entity) {
        super(entity);
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.amount = entity.getAmount();
        this.file = entity.getFile();
        this.payment = entity.getPayment();
        this.group = entity.getGroup();
    }

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double amount;

    @OneToOne(fetch = FetchType.EAGER)
    private FileEntity file;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

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

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentEntity getPayment() {
        return payment;
    }

    public void setPayment(PaymentEntity payment) {
        this.payment = payment;
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
                && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                name,
                description,
                amount,
                file);
    }

    @Override
    public String toString() {
        return "ReceiptEntity{"
                + "name='" + name + '\''
                + ", description='" + description + '\''
                + ", amount=" + amount
                + ", file=" + file
                + "} " + super.toString();
    }
}
