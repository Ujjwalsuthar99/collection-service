package com.synoriq.synofin.collection.collectionservice.entity;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;

@Transactional
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@TypeDef(name = "enum_type", typeClass = com.synoriq.synofin.collection.collectionservice.entity.EnumTypeCast.class)
@Table(name = "consumed_api_logs", schema = COLLECTION)
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

    @Column(name = "log_name", columnDefinition = "collection.consumed_api_logs_log_name")
    @Enumerated(EnumType.STRING)
    @Type(type = "enum_type")
    private EnumSQLConstants.LogNames logName;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "api_type")
    private String apiType;

    @Type(type = "json")
    @Column(name = "request_body")
    private Object requestBody;

    @Type(type = "json")
    @Column(name = "response_data")
    private Object responseData;

    @Column(name = "response_status")
    private String responseStatus;

    @Column(name = "end_point", columnDefinition = "TEXT")
    private String endPoint;

}
