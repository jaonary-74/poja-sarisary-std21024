package hei.school.sarisary.endpoint.rest.controller.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BlackAndWhiteController {
    private Map<String, ImageData> imageStore = new HashMap<>();

    @PutMapping("/black-and-white/{id}")
    public ResponseEntity<Void> processImage(@PathVariable String id, @RequestBody byte[] imageBytes) {
        try {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            BufferedImage transformedImage = convertToBlackAndWhite(originalImage);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(transformedImage, "png", outputStream);
            byte[] transformedImageBytes = outputStream.toByteArray();
            ImageData imageData = new ImageData(imageBytes, transformedImageBytes);
            imageStore.put(id, imageData);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/black-and-white/{id}")
    public ResponseEntity<ImageResponse> getImageResult(@PathVariable String id) {
        ImageData imageData = imageStore.get(id);
        if (imageData == null) {
            return ResponseEntity.notFound().build();
        }
        ImageResponse imageResponse = new ImageResponse(imageData.getOriginalImageBytes(), imageData.getTransformedImageBytes());
        return ResponseEntity.ok(imageResponse);
    }

    private BufferedImage convertToBlackAndWhite(BufferedImage image) {
        BufferedImage bwImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return bwImage;
    }

    private static class ImageData {
        private byte[] originalImageBytes;
        private byte[] transformedImageBytes;

        public ImageData(byte[] originalImageBytes, byte[] transformedImageBytes) {
            this.originalImageBytes = originalImageBytes;
            this.transformedImageBytes = transformedImageBytes;
        }

        public byte[] getOriginalImageBytes() {
            return originalImageBytes;
        }

        public byte[] getTransformedImageBytes() {
            return transformedImageBytes;
        }
    }

    private static class ImageResponse {
        private String originalUrl;
        private String transformedUrl;

        public ImageResponse(byte[] originalUrl, byte[] transformedUrl) {
            this.originalUrl = Arrays.toString(originalUrl);
            this.transformedUrl = Arrays.toString(transformedUrl);
        }

        public String getOriginalUrl() {
            return originalUrl;
        }

        public String getTransformedUrl() {
            return transformedUrl;
        }
    }
}