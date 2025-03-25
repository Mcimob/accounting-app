package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReceiptEntityFilter extends AbstractFilter<ReceiptEntity> {

    private String userInput;
    private LocalDateTime notPaidBefore;
    private GroupEntity group;

    @Override
    public Specification<ReceiptEntity> getSpecification() {
        Specification<ReceiptEntity> spec = super.getSpecification();

        if (userInput != null) {
            spec = spec.or(includesUserInput());
        }

        if (notPaidBefore != null) {
            spec = spec.and(wasNotPaidOutBefore());
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

    private Specification<ReceiptEntity> wasNotPaidOutBefore() {
        return ((root, query, cb) ->
                cb.or(cb.greaterThan(root.get("paidOutAt"), notPaidBefore), cb.isNull(root.get("paidOutAt"))));
    }

    private Specification<ReceiptEntity> belongsToGroup() {
        return ((root, query, cb) ->
                cb.equal(root.get("group"), group));
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public void setNotPaidBefore(LocalDateTime notPaidBefore) {
        this.notPaidBefore = notPaidBefore;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }
}
