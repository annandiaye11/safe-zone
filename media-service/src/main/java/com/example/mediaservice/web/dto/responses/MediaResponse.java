package com.example.mediaservice.web.dto.responses;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaResponse {
    private String id;
    private String imagePath;
    private String productId;
}
