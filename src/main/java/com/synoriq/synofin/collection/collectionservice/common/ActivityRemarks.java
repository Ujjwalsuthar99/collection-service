package com.synoriq.synofin.collection.collectionservice.common;

public class ActivityRemarks {

    public static final String CREATE_FOLLOWUP = "Follow up {request_id} has been added against {loan_number} by ";
    public static final String RESCHEDULE_FOLLOWUP = "Follow up {request_id} has been rescheduled against {loan_number} by ";
    public static final String CLOSE_FOLLOWUP = "Follow up {request_id} has been closed against {loan_number}";
    public static final String CREATE_RECEIPT = "Receipt {receipt_number} has been generated against loan number {loan_number}";
    public static final String CREATE_TRANSFER = "Transfer Request ID {transfer_request} initiated by ";
    public static final String TRANSFER_STATUS = "Transfer Request ID {transfer_request} is {transfer_action} by ";
    public static final String LOGOUT_REMARKS = "The user {user_name} has been Logged Out";
    public static final String KAFKA_RECEIPT_STATUS = "The receipt {receipt_id} has been {status} from LMS";
    public static final String USER_MESSAGE = "The SMS was sent to customer after receipt generation against {loan_number}";

}
