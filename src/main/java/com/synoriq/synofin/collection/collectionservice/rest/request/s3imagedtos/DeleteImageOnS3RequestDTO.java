package com.synoriq.synofin.collection.collectionservice.rest.request.s3imagedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class DeleteImageOnS3RequestDTO {

    @JsonProperty("data")
    private Object data;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;
}
