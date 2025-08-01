package com.example.mediaservice.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDtoAll {
    private String id;
    @NotBlank(message = "Image is required")
    @NotNull(message = "Image is required")
    private MultipartFile imagePath;
    @NotBlank(message = "Product Id is required")
    @NotNull(message = "Product Id is required")
    private String productId;
}
