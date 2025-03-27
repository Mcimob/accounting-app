package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.GroupEntity;
import ch.pfaditools.accounting.model.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserEntityFilter extends AbstractFilter<UserEntity> {

    private String username;
    private boolean exactMatch = false;
    private GroupEntity group;

    @Override
    public Specification<UserEntity> getSpecification() {
        Specification<UserEntity> spec = super.getSpecification();
        if (username != null) {
            spec = spec.and(hasUserName());
        }
        if (group != null) {
            spec = spec.and(hasGroup());
        }
        return spec;
    }

    private Specification<UserEntity> hasUserName() {
        return (root, query, cb) -> {
            if (exactMatch) {
                return cb.equal(root.get("username"), username);
            } else {
                return cb.like(root.get("username"), username + "%");
            }
        };
    }

    private Specification<UserEntity> hasGroup() {
        return (root, query, cb) ->
                cb.equal(root.get("group"), group);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }
}
