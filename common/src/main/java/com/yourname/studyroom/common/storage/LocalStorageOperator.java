package com.yourname.studyroom.common.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * 本地磁盘文件存储操作器
 */
@RequiredArgsConstructor
public class LocalStorageOperator {

    private final LocalStorageProperties properties;

    /**
     * 保存上传文件，返回可直接访问的相对 URL
     */
    public String save(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("上传文件不能为空");
        }
        String filename = new SimpleDateFormat("yyyyMMdd").format(new Date())
                + "/" + UUID.randomUUID() + "-" + safeName(file.getOriginalFilename());
        Path target = toStorageRoot().resolve(filename).normalize();
        Files.createDirectories(target.getParent());
        Files.copy(file.getInputStream(), target);
        return properties.getUrlPrefix() + "/" + filename;
    }

    /**
     * 只保留文件名主体与扩展名，避免上传方构造的路径穿越字符
     */
    private String safeName(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "file";
        }
        String name = StringUtils.getFilename(originalFilename);
        String extension = StringUtils.getFilenameExtension(name);
        String base = name.substring(0, name.length() - (extension == null ? 0 : extension.length() + 1));
        base = base.replaceAll("[^a-zA-Z0-9._-]", "_");
        return extension == null ? base : base + "." + extension.toLowerCase(Locale.ROOT);
    }

    private Path toStorageRoot() {
        return Path.of(properties.getUploadDir()).toAbsolutePath().normalize();
    }
}
