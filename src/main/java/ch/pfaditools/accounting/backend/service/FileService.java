package ch.pfaditools.accounting.backend.service;

import ch.pfaditools.accounting.model.entity.FileEntity;
import ch.pfaditools.accounting.model.filter.FileEntityFilter;

import java.io.InputStream;

public interface FileService extends BaseService<FileEntity, FileEntityFilter> {
    ServiceResponse<FileEntity> saveWithFile(FileEntity fileEntity, InputStream inputStream);

    ServiceResponse<InputStream> getFileContent(FileEntity fileEntity);

    ServiceResponse<FileEntity> deleteFile(FileEntity fileEntity);
}
