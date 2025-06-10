package com.study.authenticationservice.Validator;

import com.amazonaws.services.kafka.model.S3;
import com.study.authenticationservice.exception.AppException;
import com.study.authenticationservice.exception.ErrorCode;
import com.study.authenticationservice.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ValidateImage {
    @Autowired
    private S3Service s3Service;
    public String ValidateUploadFile(MultipartFile avatar) throws AppException {
        try {
            if (avatar == null || avatar.isEmpty()) {
                throw new AppException(ErrorCode.FILE_EMPTY);
            }
            return s3Service.uploadFile(avatar);
        } catch (Exception e) {
            // Log lỗi chi tiết và ném lỗi ứng dụng
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
    }
}
