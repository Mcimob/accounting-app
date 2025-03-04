package ch.pfaditools.accounting.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "group_entity")
public class GroupEntity extends AbstractEntity {

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "group")
    private Set<UserEntity> users = new HashSet<>();

    private String groupCode;

    private String groupAdminCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupAdminCode() {
        return groupAdminCode;
    }

    public void setGroupAdminCode(String groupAdminCode) {
        this.groupAdminCode = groupAdminCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GroupEntity that = (GroupEntity) o;
        return super.equals(that) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
        return "GroupEntity{" +
                "name='" + name + '\'' +
                ", users=" + users +
                '}' + super.toString();
    }
}
