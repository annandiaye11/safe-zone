package com.example.mediaservice.utils.mappers;

import com.example.mediaservice.data.entities.Media;
import com.example.mediaservice.services.impl.S3Service;
import com.example.mediaservice.web.dto.requests.MediaDtoAll;
import com.example.mediaservice.web.dto.responses.MediaResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MapperMedia {
    private static final S3Service s3Service = new S3Service();

    public static Media toEntity(MediaDtoAll mediaDtoAll) {
        Media media = new Media();
        media.setImagePath(s3Service.uploadFile(mediaDtoAll.getImagePath()));
        media.setProductId(mediaDtoAll.getProductId());
        return media;
    }



    public static MediaResponse toDto(Media media) {
        MediaResponse mediaResponse = new MediaResponse();
        mediaResponse.setId(media.getId());
        mediaResponse.setImagePath(media.getImagePath());
        mediaResponse.setProductId(media.getProductId());
        return mediaResponse;
    }


}
