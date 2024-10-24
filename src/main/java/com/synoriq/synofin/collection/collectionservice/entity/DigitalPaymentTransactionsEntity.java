package com.synoriq.synofin.collection.collectionservice.entity;

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
@Table(name = "digital_payment_transactions", schema = COLLECTION)
@Data
public class DigitalPaymentTransactionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "digital_payment_trans_id", nullable = false)
    private Long digitalPaymentTransactionsId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Date createdDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Date modifiedDate;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "loan_id")
    private Long loanId;

    @Column(name = "payment_service_name")
    private String paymentServiceName;

    @Column(name = "status")
    private String status;

    @Column(name = "merchant_tran_id")
    private String merchantTranId;

    @Column(name = "amount")
    private Float amount;

    @Column(name = "utr_number")
    private String utrNumber;

    @Column(name = "receipt_request_body")
    @Type(type = "json")
    private Object receiptRequestBody;

    @Column(name = "payment_link", columnDefinition = "TEXT")
    private String paymentLink;

    @Column(name = "mobile_no")
    private Long mobileNo;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "receipt_generated")
    private Boolean receiptGenerated;

    @Column(name = "receipt_response")
    @Type(type = "json")
    private Object receiptResponse;

    @Column(name = "collection_activity_logs_id")
    private Long collectionActivityLogsId;

    @Column(name = "action_activity_logs_id")
    private Long actionActivityLogsId;

    @Column(name = "other_response_data")
    @Type(type = "json")
    private Object otherResponseData;

    @Column(name = "call_back_request_body")
    @Type(type = "json")
    private Object callBackRequestBody;

    public DigitalPaymentTransactionsEntity(Date createdDate, Long createdBy,
                                            Date modifiedDate, Long modifiedBy,
                                            Long loanId, String paymentServiceName,
                                            String status, String merchantTranId,
                                            Float amount, String utrNumber,
                                            Object receiptRequestBody, String paymentLink,
                                            Long mobileNo, String vendor,
                                            Boolean receiptGenerated, Object receiptResponse,
                                            Long collectionActivityLogsId, Long actionActivityLogsId,
                                            Object otherResponseData, Object callBackRequestBody) {
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        this.loanId = loanId;
        this.paymentServiceName = paymentServiceName;
        this.status = status;
        this.merchantTranId = merchantTranId;
        this.amount = amount;
        this.utrNumber = utrNumber;
        this.receiptRequestBody = receiptRequestBody;
        this.paymentLink = paymentLink;
        this.mobileNo = mobileNo;
        this.vendor = vendor;
        this.receiptGenerated = receiptGenerated;
        this.receiptResponse = receiptResponse;
        this.collectionActivityLogsId = collectionActivityLogsId;
        this.actionActivityLogsId = actionActivityLogsId;
        this.otherResponseData = otherResponseData;
        this.callBackRequestBody = callBackRequestBody;
    }

    public DigitalPaymentTransactionsEntity() {

    }

}
