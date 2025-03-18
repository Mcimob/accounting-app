package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.FileEntity;
import org.springframework.content.commons.store.ContentStore;
import org.springframework.stereotype.Repository;

@Repository
public interface FileContentRepository extends ContentStore<FileEntity, Long> {
}
