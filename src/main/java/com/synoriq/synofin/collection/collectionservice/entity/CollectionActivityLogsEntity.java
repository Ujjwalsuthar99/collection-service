//package com.synoriq.synofin.collection.collectionservice.entity;
//
//import com.synoriq.synofin.collection.collectionservice.common.dto.UserAddress;
//import lombok.Data;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import javax.transaction.Transactional;
//import java.util.Date;
//
//@Getter
//@Setter
//@Transactional
//@Entity
//@Table(name = "collection_activity_logs")
//@Data
//public class CollectionActivityLogsEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "collection_activity_logs_id", nullable = false)
//    private Long collectionActivityLogsId;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "activity_date")
//    private Date activityDate;
//
//    @Column(name = "activity_by")
//    private Long activityBy;
//
//    @Column(name = "deleted")
//    private Boolean deleted;
//
//    @Column(name = "activity_name")
//    private String activityName;
//
//    @Column(name = "distance_from_user_branch")
//    private Integer distanceFromUserBranch;
//
//    @Column(name = "remarks", columnDefinition = "TEXT")
//    private String remarks;
//
//    @Column(name = "loan_id")
//    private Long loanId;
//
//    @Convert()
//    @Column(name = "address")
//    private UserAddress address;
//
//
//
//}
