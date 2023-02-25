package com.synoriq.synofin.collection.collectionservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

@Transactional
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "collection_limit_userwise", schema = "collection")
@Data
public class CollectionLimitUserWiseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_limit_userwise_id", nullable = false)
    private Long collectionLimitUserwiseId;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "collection_limit_strategies_key")
    private String collectionLimitStrategiesKey;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_limit_value")
    private Double totalLimitValue;

    @Column(name = "utilized_limit_value")
    private Double utilizedLimitValue;

}
