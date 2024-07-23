package com.synoriq.synofin.collection.collectionservice.rest.request.emitraRequestDTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifySsoTokenDTO {

    @JsonProperty("token")
    private String token;

}