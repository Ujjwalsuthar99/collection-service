package com.synoriq.synofin.collection.collectionservice.rest.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeviceStatusUpdateDTORequest {

    @JsonProperty("registered_device_info_id")
    private Long registeredDeviceInfoId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("status")
    private String status;

}
