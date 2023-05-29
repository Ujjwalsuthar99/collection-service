package com.synoriq.synofin.collection.collectionservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

@Transactional
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "collection_activity_logs", schema = "collection")
@Data
public class ConsumedApiLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumed_api_logs_id", nullable = false)
    private Long consumedApiLogsId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Date createdDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "activity_name")
    private String activityName;

    @Column(name = "distance_from_user_branch")
    private Double distanceFromUserBranch;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "address")
    @Type(type = "json")
    private Object address;

    @Column(name = "images")
    @Type(type = "json")
    private Object images;

    @Column(name = "geo_location_data")
    @Type(type = "json")
    private Object geolocation;

}
