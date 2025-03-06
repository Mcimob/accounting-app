package ch.pfaditools.accounting.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_entity")
public class UserEntity extends AbstractEntity implements UserDetails {

    @Column(nullable = false, unique = true)
    private String username;

    private String displayName;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(username, that.username)
                && Objects.equals(displayName, that.displayName)
                && Objects.equals(password, that.password)
                && Objects.equals(roles, that.roles)
                && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                username,
                displayName,
                password,
                roles,
                group);
    }

    @ManyToOne(fetch = FetchType.EAGER)
    private GroupEntity group;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "UserEntity{"
                + "roles=" + roles
                + ", username='" + username + '\''
                + ", displayName='" + displayName + '\''
                + ", password='" + password + '\''
                + ", group=" + group
                + "} " + super.toString();
    }

}
