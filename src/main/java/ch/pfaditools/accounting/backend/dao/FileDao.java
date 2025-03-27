package ch.pfaditools.accounting.backend.dao;

import ch.pfaditools.accounting.backend.dao.exception.DaoException;
import ch.pfaditools.accounting.model.entity.FileEntity;
import ch.pfaditools.accounting.model.filter.FileEntityFilter;

import java.io.InputStream;
import java.util.Optional;

public interface FileDao extends BaseDao<FileEntity, FileEntityFilter> {

    Optional<FileEntity> saveWithFile(FileEntity fileEntity, InputStream file) throws DaoException;

    Optional<InputStream> getFileContent(FileEntity fileEntity) throws DaoException;

    void deleteFileContent(FileEntity fileEntity) throws DaoException;

}
