package songservice.streamify.utils;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.buckets.track}")
    private String trackBucket;

    @Value("${minio.buckets.artwork}")
    private String artworkBucket;

    @PostConstruct
    public void initBuckets() {
        createBucketIfNotExists(trackBucket);
        createBucketIfNotExists(artworkBucket);
    }

    private void createBucketIfNotExists(String bucket) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created MinIO bucket: {}", bucket);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize MinIO bucket: " + bucket, e);
        }
    }

    public String uploadFile(MultipartFile file, String bucket, String objectPrefix) throws FileUploadException {
        String objectName = objectPrefix + UUID.randomUUID() + getExtension(file.getOriginalFilename());

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectName)
                    .expiry(7, TimeUnit.DAYS)
                    .build());

        } catch (Exception e) {
            log.error("Upload failed for file: {}", file.getOriginalFilename(), e);
            throw new FileUploadException("Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }

    public void deleteObject(String bucket, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.warn("Failed to delete object {}/{}", bucket, objectName, e);
        }
    }

    public String extractObjectNameFromUrl(String url, String bucket) {
        try {
            return URLDecoder.decode(url.split(bucket + "/")[1].split("\\?")[0], StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.debug("Could not extract object name from URL: {}", url);
            return null;
        }
    }

    private String getExtension(String filename) {
        return filename != null && filename.contains(".")
                ? filename.substring(filename.lastIndexOf("."))
                : "";
    }
}
