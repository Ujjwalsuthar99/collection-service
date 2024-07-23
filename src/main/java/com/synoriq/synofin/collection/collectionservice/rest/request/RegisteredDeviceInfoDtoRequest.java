package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RegisteredDeviceInfoDtoRequest {


    Long createdBy;
    Boolean deleted;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    Date lastAppUsage;
    String currentAppVersion;
    String platform;
    String platformVersion;
    String deviceUniqueId;
    String deviceManufacturerName;
    String deviceModel;
    String status;

}
