package com.synoriq.synofin.collection.collectionservice.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Transactional
@Entity
@Table(name = "followups", schema = "collection")
public class FollowUpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "followups_id", nullable = false)
    private Long followupId;

    @Column(name = "loan_id")
    protected Long loanId;

    @Column(name = "deleted")
    protected Boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    protected Date createdDate;

    @Column(name = "created_by")
    protected Long createdBy;

    @Column(name = "followup_reason")
    protected String followUpReason;

    @Column(name = "other_followup_reason")
    protected String otherFollowUpReason;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_followup_datetime")
    protected Date nextFollowUpDateTime;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "collection_activity_logs_id")
    private Long collectionActivityLogsId;

    @Column(name = "followup_status")
    private String followUpStatus;

    @Column(name = "closing_remarks")
    private String closingRemarks;

    @Column(name = "service_request_id")
    private Long serviceRequestId;
}
