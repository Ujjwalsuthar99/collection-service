package com.synoriq.synofin.collection.collectionservice.rest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredDeviceInfoDTO {

    private Long id;
    private Date createdDate;
    private Long createdBy;
    private Boolean deleted;
    private Long userId;
    private Date lastAppUsage;
    private String currentAppVersion;
    private String platform;
    private String platformVersion;
    private String deviceUniqueId;
    private String deviceManufacturerName;
    private String deviceModel;
    private String status;

}
