package com.synoriq.synofin.collection.collectionservice.rest.request.s3ImageDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageOnS3DataRequestDTO {

    @JsonProperty("reference_path")
    private String userRefNo;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file")
    private String file;

    @JsonProperty("file_content_type")
    private String fileContentType;
}
