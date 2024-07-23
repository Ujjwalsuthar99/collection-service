package com.synoriq.synofin.collection.collectionservice.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;

@Transactional
@Entity
@Table(name = "registered_device_info", schema = COLLECTION)
@Data
public class RegisteredDeviceInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registered_device_info_id", nullable = false)
    private Long registeredDeviceInfoId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "last_app_usages")
    private Date lastAppUsage;

    @Column(name = "current_app_version")
    private String currentAppVersion;

    @Column(name = "platform")
    private String platform;

    @Column(name = "platform_version")
    private String platformVersion;

    @Column(name = "device_unique_id")
    private String deviceUniqueId;

    @Column(name = "device_manufacturer_name")
    private String deviceManufacturerName;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "status")
    private String status;

    

}
