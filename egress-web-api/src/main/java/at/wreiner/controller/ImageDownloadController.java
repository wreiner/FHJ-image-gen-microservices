package at.wreiner.controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/egress")
public class ImageDownloadController {
    @Autowired
    private AmazonS3 s3client;

    @Value("${s3.s3_bucket}")
    private String bucketName;

    @GetMapping("/download-image/{uuid}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable UUID uuid) {
        try {
            S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, uuid.toString() + ".png"));
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(bytes);
        } catch (AmazonServiceException e) {
            if (e.getStatusCode() == 404) {
                // Object not found
                return ResponseEntity.notFound().build();
            } else {
                // Other S3 errors
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
