package ch.pfaditools.accounting.backend.service.impl;

import ch.pfaditools.accounting.backend.dao.FileDao;
import ch.pfaditools.accounting.backend.dao.exception.ContentDaoException;
import ch.pfaditools.accounting.backend.dao.exception.DaoException;
import ch.pfaditools.accounting.backend.service.FileService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.FileEntity;
import ch.pfaditools.accounting.model.filter.FileEntityFilter;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FileServiceImpl extends BaseServiceImpl<FileEntity, FileEntityFilter> implements FileService {

    private final FileDao dao;

    public FileServiceImpl(FileDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    public ServiceResponse<FileEntity> saveWithFile(FileEntity fileEntity, InputStream file) {
        ServiceResponse<FileEntity> response = new ServiceResponse<>();
        if (handleNullCheck(response, fileEntity, file)) {
            return response;
        }

        try {
            response.setEntity(dao.saveWithFile(fileEntity, file).orElse(null));
            return response;
        } catch (ContentDaoException e) {
            response.addErrorMessage("service.file.saveWithFile.error");
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error saving entity with file");
        }
    }

    @Override
    public ServiceResponse<InputStream> getFileContent(FileEntity fileEntity) {
        ServiceResponse<InputStream> response = new ServiceResponse<>();
        if (handleNullCheck(response, fileEntity)) {
            return response;
        }

        try {
            dao.getFileContent(fileEntity).ifPresent(response::setEntity);
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error getting file content");
        }
    }

    @Override
    public ServiceResponse<FileEntity> deleteFile(FileEntity fileEntity) {
        ServiceResponse<FileEntity> response = new ServiceResponse<>();
        if (handleNullCheck(response, fileEntity)) {
            return response;
        }

        try {
            dao.deleteFileContent(fileEntity);
            dao.delete(fileEntity);
            return response;
        } catch (DaoException e) {
            return handleException(e, "Error deleting file");
        }
    }
}
