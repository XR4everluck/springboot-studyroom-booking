package com.yourname.studyroom.web.admin.service.impl;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.common.storage.LocalStorageOperator;
import com.yourname.studyroom.web.admin.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

@Service
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");

    @Autowired
    LocalStorageOperator localStorageOperator;

    /**
     * 文件上传
     *
     * @param file 用户上传的文件
     * @return 文件链接
     */
    @Override
    public String upload(MultipartFile file) throws IOException {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new BookingException(ResultCodeEnum.PARAM_ERROR.getCode(), "仅支持上传 jpg/jpeg/png/gif/webp/bmp 图片");
        }
        return localStorageOperator.save(file);
    }
}
