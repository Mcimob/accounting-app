package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.FileEntity;
import ch.pfaditools.accounting.model.filter.FileEntityFilter;
import org.springframework.stereotype.Repository;

@Repository
public interface FileEntityRepository
        extends BaseRepository<FileEntity, FileEntityFilter> {
}
