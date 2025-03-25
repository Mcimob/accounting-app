package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import org.springframework.data.jpa.domain.Specification;

public class ReceiptEntityFilter extends AbstractFilter<ReceiptEntity> {

    private String userInput;
    private Boolean paidOut;
    private GroupEntity group;

    @Override
    public Specification<ReceiptEntity> getSpecification() {
        Specification<ReceiptEntity> spec = super.getSpecification();

        if (userInput != null) {
            spec = spec.or(includesUserInput());
        }

        if (paidOut != null) {
            spec = spec.and(isPaidOut());
        }

        if (group != null) {
            spec = spec.and(belongsToGroup());
        }

        return spec;
    }

    private Specification<ReceiptEntity> includesUserInput() {
        return ((root, query, cb) ->
                cb.or(
                        cb.like(root.get("name"), userInput + "%"),
                        cb.like(root.get("createdUser"), userInput + "%")));
    }

    private Specification<ReceiptEntity> isPaidOut() {
        return ((root, query, cb) ->
                paidOut ? cb.isNotNull(root.get("payment")) : cb.isNull(root.get("payment")));
    }

    private Specification<ReceiptEntity> belongsToGroup() {
        return ((root, query, cb) ->
                cb.equal(root.get("group"), group));
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public void setPaidOut(Boolean paidOut) {
        this.paidOut = paidOut;
    }
}
