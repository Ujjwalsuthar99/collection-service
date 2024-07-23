package com.synoriq.synofin.collection.collectionservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Date;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;

@Transactional
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "receipt_transfer", schema = COLLECTION)
@Data
public class ReceiptTransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_transfer_id", nullable = false)
    private Long receiptTransferId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "transferred_by")
    private Long transferredBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "transfer_type")
    private String transferType;

    @Column(name = "transfer_mode")
    private String transferMode;

    @Column(name = "transferred_to_user_id")
    private Long transferredToUserId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "receipt_image")
    @Type(type = "json")
    private Object receiptImage;

    @Column(name = "status")
    private String status;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "transfer_bank_code")
    private String transferBankCode;

    @Column(name = "action_datetime")
    private Date actionDatetime;

    @Column(name = "action_reason")
    private String actionReason;

    @Column(name = "action_remarks")
    private String actionRemarks;

    @Column(name = "action_by")
    private Long actionBy;

    @Column(name = "collection_activity_logs_id")
    private Long collectionActivityLogsId;

    @Column(name = "action_activity_logs_id")
    private Long actionActivityLogsId;


    

}
