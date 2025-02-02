package com.synoriq.synofin.collection.collectionservice.common;

public class ActivityEvent {

    private ActivityEvent() {

    }
    public static final String LOGIN = "login";
    public static final String APP_ACCESS = "app_access";
    public static final String CREATE_RECEIPT = "create_receipt";
    public static final String CREATE_FOLLOWUP = "create_followup";
    public static final String ADD_ADDITIONAL_CONTACT = "add_additional_contact";
    public static final String RECEIPT_TRANSFER = "receipt_transfer";
    public static final String RECEIPT_TRANSFER_CANCEL = "cancelled";
    public static final String RECEIPT_TRANSFER_REJECT = "rejected";
    public static final String RECEIPT_TRANSFER_APPROVE = "approved";
    public static final String RECEIPT_TRANSFER_PENDING = "pending";
    public static final String LOGOUT = "logout";


}
