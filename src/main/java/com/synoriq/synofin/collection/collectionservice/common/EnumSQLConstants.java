package com.synoriq.synofin.collection.collectionservice.common;

public class EnumSQLConstants {
    public enum LogNames {
        create_receipt,
        get_master_type,
        get_data_for_loan_action,
        cheque_ocr,
        s3_upload,
        sms_service,
        shorten_url,
        s3_download,
        get_basic_loan_detail,
        get_customer_details,
        get_receipt_date,
        get_token_details,
        global_search,
        get_profile_details,
        fetch_all_user_data,
        contact_support,
        get_pdf,
        razor_pay_ifsc,
        deposit_challan,
        get_user_details_admin;

        private LogNames() {
        }
    }
}
