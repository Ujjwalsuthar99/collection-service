package com.synoriq.synofin.collection.collectionservice.rest.request.s3ImageDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UploadImageOnS3RequestDTO {

    @JsonProperty("data")
    private UploadImageOnS3DataRequestDTO data;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;
}
