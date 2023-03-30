package com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageData {

    @JsonProperty("user_ref_no")
    private String userRefNo;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file")
    private String file;

    @JsonProperty("file_content_type")
    private String fileContentType;
}
