package ch.pfaditools.accounting.backend.dao.impl;

import ch.pfaditools.accounting.backend.dao.FileDao;
import ch.pfaditools.accounting.backend.dao.exception.ContentDaoException;
import ch.pfaditools.accounting.backend.dao.exception.DaoException;
import ch.pfaditools.accounting.backend.repository.FileContentRepository;
import ch.pfaditools.accounting.backend.repository.FileEntityRepository;
import ch.pfaditools.accounting.model.entity.FileEntity;
import ch.pfaditools.accounting.model.filter.FileEntityFilter;
import org.springframework.content.commons.store.StoreAccessException;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

@Component
public class FileDaoImpl extends BaseDaoImpl<FileEntity, FileEntityFilter> implements FileDao {

    private final FileContentRepository contentRepository;

    public FileDaoImpl(FileEntityRepository repository, FileContentRepository contentRepository) {
        super(repository);
        this.contentRepository = contentRepository;
    }

    public Optional<FileEntity> saveWithFile(FileEntity fileEntity, InputStream file) throws DaoException {
        try {
            contentRepository.setContent(fileEntity, file);
        } catch (StoreAccessException e) {
            throw new ContentDaoException("Error while saving file", e);
        }
        return save(fileEntity);
    }

    @Override
    public Optional<InputStream> getFileContent(FileEntity fileEntity) throws DaoException {
        try {
            return Optional.ofNullable(contentRepository.getContent(fileEntity));
        } catch (StoreAccessException e) {
            throw new ContentDaoException("Error while fetching file content", e);
        }
    }

    @Override
    public void deleteFileContent(FileEntity fileEntity) throws DaoException {
        try {
            contentRepository.unsetContent(fileEntity);
        } catch (StoreAccessException e) {
            throw new ContentDaoException("Error while deleting file content", e);
        }
    }
}
