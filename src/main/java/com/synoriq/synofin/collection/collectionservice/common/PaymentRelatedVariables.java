package com.synoriq.synofin.collection.collectionservice.common;

public class PaymentRelatedVariables {

    public static final String SEND_QR_CODE_GENERATE_API = "http://localhost:1102/v1/sendQrCode";
    public static final String SEND_QR_CODE_TRANSACTION_STATUS_API = "http://localhost:1102/v1/getQrCodeTransactionStatus";
    public static final String GET_PDF_API = "http://localhost:1102/v1/getPdf?deliverableType=receipt_details&serviceRequestId=";
    public static final String DYNAMIC_QR_CODE = "dynamic_qr_code";
    public static final String PAYMENT_LINK = "payment_link";
    public static final String KOTAK_VENDOR = "kotak";
    public static final String PENDING = "pending";
    public static final String SUCCESS = "success";
    public static final String PAID = "paid";
    public static final String FAILURE = "failure";
    public static final String QR_CALLBACK_SUCCESS = "SUCCESS";
    public static final String UPI = "upi";
    public static final String STATUS = "status";
    public static final String RECEIPT_GENERATED = "receipt_generated";
    public static final String SR_ID = "service_request_id";
    public static final String CONNECTOR_RESPONSE = "connector_response";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENTTYPE = "Content-Type";
    public static final String SEND_PAYMENT_LINK = "http://localhost:1102/v1/sendPaymentLink";
    public static final String PAYMENT_LINK_TRANSACTION_CHECK = "http://localhost:1102/v1/paymentLinkStatusCheck";
    public static final String RAZORPAY = "razorpay";

}
