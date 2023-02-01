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
@Table(name = "additional_contact_details",schema = "collection")
public class AdditionalContactDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "additional_contact_detail_id", nullable = false)
    private Long additionalContactDetailId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "mobile_no")
    private Integer mobileNumber;

    @Column(name = "alt_mobile_no")
    private Integer altMobileNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "relation_with_applicant")
    private String relationWithApplication;

    

}
