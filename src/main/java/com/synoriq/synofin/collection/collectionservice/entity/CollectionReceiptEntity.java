package com.synoriq.synofin.collection.collectionservice.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.transaction.Transactional;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;

@Transactional
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "collection_receipts", schema = COLLECTION)
@Data
public class CollectionReceiptEntity {

    @Id
    @Column(name = "receipt_id", nullable = false)
    private Long receiptId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "receipt_holder_user_id")
    private Long receiptHolderUserId;

    @Column(name = "last_receipt_transfer_id")
    private Long lastReceiptTransferId;

    @Column(name = "collection_activity_logs_id")
    private Long collectionActivityLogsId;

}
