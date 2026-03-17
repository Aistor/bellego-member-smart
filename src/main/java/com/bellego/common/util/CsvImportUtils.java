package com.bellego.common.util;

import com.bellego.common.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class CsvImportUtils {

    public <T> List<T> read(MultipartFile file, Function<String[], T> converter) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        List<T> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (firstLine && looksLikeHeader(parts)) {
                    firstLine = false;
                    continue;
                }
                firstLine = false;
                result.add(converter.apply(parts));
            }
        } catch (IOException ex) {
            throw new BusinessException("读取导入文件失败");
        }
        return result;
    }

    private boolean looksLikeHeader(String[] parts) {
        return parts.length > 0 && parts[0].matches("[A-Za-z_\u4e00-\u9fa5]+.*");
    }
}

