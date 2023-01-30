package com.synoriq.synofin.collection.collectionservice.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

@Getter
@Setter
@Transactional
@Entity
@Table(name = "collection_configurations")
public class CollectionConfigurationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "configuration_id", nullable = false)
    private Long configurationsId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "configuration_name")
    private String configurationName;

    @Column(name = "configuration_value")
    private String configurationValue;

    @Column(name = "configuration_description", columnDefinition = "TEXT")
    private String configurationDescription;


}
