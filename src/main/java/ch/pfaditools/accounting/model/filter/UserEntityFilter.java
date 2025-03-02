package ch.pfaditools.accounting.model.filter;

import ch.pfaditools.accounting.model.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserEntityFilter extends AbstractFilter<UserEntity> {

    private String username;

    @Override
    public Specification<UserEntity> getSpecification() {
        return (root, query, criteriaBuilder) ->
                username != null ? criteriaBuilder.equal(root.get("username"), username) : null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
