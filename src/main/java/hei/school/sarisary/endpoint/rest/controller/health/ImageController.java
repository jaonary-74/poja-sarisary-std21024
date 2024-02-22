package hei.school.sarisary.endpoint.rest.controller.health;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/black-and-white")
public class ImageController {
    private Map<String, ImageData> imageMap = new HashMap<>();

    @PutMapping("/{id}")
    public ResponseEntity<Void> convertToBlackAndWhite(@PathVariable("id") String id, @RequestBody byte[] imageData) {
        if (imageMap.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Retourner une réponse 409 Conflict
        }

        ImageData image = new ImageData(imageData);
        imageMap.put(id, image);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, String>> getImageUrls(@PathVariable("id") String id) {
        if (!imageMap.containsKey(id)) {
            return ResponseEntity.notFound().build(); // Retourner une réponse 404 Not Found
        }

        ImageData image = imageMap.get(id);

        String originalUrl = generatePreSignedUrl(image.getOriginalImageData());
        String transformedUrl = generatePreSignedUrl(image.getTransformedImageData());

        Map<String, String> response = new HashMap<>();
        response.put("original_url", originalUrl);
        response.put("transformed_url", transformedUrl);

        return ResponseEntity.ok(response);
    }

    private String generatePreSignedUrl(byte[] imageData) {
        return "https://example.com/pre-signed-url";
    }

    class ImageData {
        private byte[] originalImageData;
        private byte[] transformedImageData;

        public ImageData(byte[] originalImageData) {
            this.originalImageData = originalImageData;
            this.transformedImageData = convertToBlackAndWhite(originalImageData);
        }

        public byte[] getOriginalImageData() {
            return originalImageData;
        }

        public byte[] getTransformedImageData() {
            return transformedImageData;
        }

        private byte[] convertToBlackAndWhite(byte[] imageData) {
            return imageData;
        }
    }
}