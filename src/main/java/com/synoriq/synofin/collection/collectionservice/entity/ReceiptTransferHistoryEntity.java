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
@Table(name = "receipt_transfer_history", schema = "collection")
@Data
public class ReceiptTransferHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_transfer_history_id", nullable = false)
    private Long receiptTransferHistoryId;

    @Column(name = "receipt_transfer_id")
    private Long receiptTransferId;

    @Column(name = "collection_receipts_id")
    private Long collectionReceiptsId;

}
