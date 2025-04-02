package ch.pfaditools.accounting.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "file_entity")
public class FileEntity extends AbstractEntity implements Serializable {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @ContentId
    @Column(nullable = false)
    private UUID contentId;

    @ContentLength
    @Column(nullable = false)
    private long contentLength;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public UUID getContentId() {
        return contentId;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FileEntity that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return contentLength == that.contentLength
                && Objects.equals(fileName, that.fileName)
                && Objects.equals(mimeType, that.mimeType)
                && Objects.equals(contentId, that.contentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                fileName,
                mimeType,
                contentId,
                contentLength);
    }

    @Override
    public String toString() {
        return "FileEntity{"
                + "fileName='" + fileName + '\''
                + ", mimeType='" + mimeType + '\''
                + ", contentId=" + contentId
                + ", contentLength=" + contentLength
                + "} " + super.toString();
    }
}
