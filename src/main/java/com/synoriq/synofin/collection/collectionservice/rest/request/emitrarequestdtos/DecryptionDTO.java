package com.synoriq.synofin.collection.collectionservice.rest.request.emitrarequestdtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DecryptionDTO {

    @JsonProperty("encrypted_data")
    private String encryptedData;

}
