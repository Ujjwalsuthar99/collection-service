package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TokenDTOResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public String data;

    @JsonProperty("error")
    public String error;

}

