package ch.pfaditools.accounting.model.entity;

import ch.pfaditools.accounting.util.DateTimeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    public AbstractEntity() { }

    public AbstractEntity(AbstractEntity entity) {
        this.id = entity.getId();
        this.createdUser = entity.getCreatedUser();
        this.updatedUser = entity.getUpdatedUser();
        this.createdDateTime = entity.getCreatedDateTime();
        this.updatedDateTime = entity.getUpdatedDateTime();
        this.version = entity.getVersion();
    }

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
    private int version;

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

    public String getCreatedDateTimeString(Locale locale) {
        if (createdDateTime == null) {
            return "";
        }
        return DateTimeUtils.formatDateTime(createdDateTime, locale);
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

    public String getUpdatedDateTimeString(Locale locale) {
        if (updatedDateTime == null) {
            return "";
        }
        return DateTimeUtils.formatDateTime(updatedDateTime, locale);
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AbstractEntity that)) {
            return false;
        }
        return version == that.version
                && Objects.equals(id, that.id)
                && Objects.equals(createdUser, that.createdUser)
                && Objects.equals(createdDateTime, that.createdDateTime)
                && Objects.equals(updatedUser, that.updatedUser)
                && Objects.equals(updatedDateTime, that.updatedDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                createdUser,
                createdDateTime,
                updatedUser,
                updatedDateTime,
                version);
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

    @Override
    public String toString() {
        return "AbstractEntity{"
                + "id=" + id
                + ", createdUser='" + createdUser + '\''
                + ", createdDateTime=" + createdDateTime
                + ", updatedUser='" + updatedUser + '\''
                + ", updatedDateTime=" + updatedDateTime
                + ", version=" + version
                + '}';
    }

}
