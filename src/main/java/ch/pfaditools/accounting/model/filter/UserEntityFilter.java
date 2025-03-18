package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserEntityFilter extends AbstractFilter<UserEntity> {

    private String username;
    private boolean exactMatch = false;

    @Override
    public Specification<UserEntity> getSpecification() {
        Specification<UserEntity> spec = super.getSpecification();
        if (username != null) {
            spec = spec.and(hasUserName());
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }
}
