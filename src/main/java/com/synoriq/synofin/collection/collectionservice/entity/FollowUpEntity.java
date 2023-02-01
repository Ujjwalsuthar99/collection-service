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
    @Column(name = "followup_id", nullable = false)
    private Long followUpId;

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
    @Column(name = "followup_datetime")
    protected Date followUpDateTime;

    @Column(name = "remarks")
    protected String remarks;
}
