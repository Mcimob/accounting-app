package ch.pfaditools.accounting.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String createdUser;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column
    private String updatedUser;

    @Column
    private LocalDateTime updatedDateTime;

    @Version
    int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public int getVersion() {
        return version;
    }

    public String getLatestUpdatedUser() {
        if (updatedUser != null) {
            return updatedUser;
        }
        return createdUser;
    }

    public void updateCreateModifyFields(String username) {
        if (id == null) {
            createdUser = username;
            createdDateTime = LocalDateTime.now();
        } else {
            updatedUser = username;
            updatedDateTime = LocalDateTime.now();
        }
    }
}
