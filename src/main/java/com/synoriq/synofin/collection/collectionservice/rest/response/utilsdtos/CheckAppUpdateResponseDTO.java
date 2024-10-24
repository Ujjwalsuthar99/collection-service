package com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class CheckAppUpdateResponseDTO {

    @JsonProperty("current_app_version")
    String currentAppVersion;

    @JsonProperty("force_update_version")
    String forceAppUpdateVersion;

    @JsonProperty("current_update_version")
    String currentAppUpdateVersion;

    @JsonProperty("is_update")
    Boolean isUpdate;

    @JsonProperty("is_force_update")
    Boolean isForceUpdate;

}
