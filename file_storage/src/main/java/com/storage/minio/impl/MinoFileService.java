package com.storage.minio.impl;

import com.storage.FileService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class MinoFileService implements FileService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String upload(byte[] content) {
        try {
            var id = UUID.randomUUID().toString();
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(id)
                            .stream(new ByteArrayInputStream(content), content.length,-1)
                        .build());
            return id;
        }
        catch (Exception e) {
            throw new RuntimeException("Upload Error", e);
        }
    }

    @Override
    public byte[] dowload(String id) {
        var args = GetObjectArgs.builder()
                .bucket(minioConfig.getBucket())
                .object(id)
                .build();
        try (var in  = minioClient.getObject(args)){
            return in.readAllBytes();
        }catch (Exception e){
            throw new RuntimeException("Download file error"+id);
        }
    }
}
