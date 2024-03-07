package com.synoriq.synofin.collection.collectionservice.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalVariables {
    public static List<String> clientMasterList = new ArrayList<>();
    public static final Map<String, String> tokenClients= new HashMap<>();

    public static final String FORCE_APP_UPDATE_VERSION_ANDROID = "force_app_update_version_android";
    public static final String CURRENT_APP_VERSION_ANDROID = "current_app_version_android";
    public static final String CHEQUE_COLLECTION_DEFAULT_LIMIT = "cheque_collection_default_limit";
    public static final String RECEIPT_TIME_VALIDATE = "duplicate_receipt_time_validation";
    public static final String EMPLOYEE_MOBILE_NUMBER_VALIDATION = "employee_mobile_number_validation";
    public static final String MONTH_CASH_VALIDATION = "monthly_x_rupees_cash_collection";
    public static final String ONLINE_COLLECTION_DEFAULT_LIMIT = "online_collection_default_limit";
    public static final String CASH_COLLECTION_DEFAULT_LIMIT = "cash_collection_default_limit";
    public static final String DISABLE_APPROVE_BUTTON_IN_LMS = "disable_approve_button_in_lms";
    public static final String DEPOSIT_REMINDER = "deposit_reminder";
    public static final String DEPOSIT_REMINDER_HOURS = "deposit_reminder_hours";
    public static final String DEPOSIT_REMINDER_DAYTIME = "deposit_reminder_after_daytime";
    public static final String SHORTEN_URL_PREPROD = "https://api-preprod.synofin.tech/integration-connector/v1/url/shortan";

    public static final String INTEGRATION_MSG_API_URL = "https://api-preprod.synofin.tech/integration-connector/v1/send-sms";
    public static final String FINOVA_MSG_API_URL = "https://api.msg91.com/api/v5/flow/";

    public static final String PAISABUDDY_MSG_API_URL = "https://api.msg91.com/api/v5/flow/";
    public static final String CSL_MSG_API_URL = "http://foxxsms.net/sms//submitsms.jsp";
    public static final String USE_BUSINESS_DATE_AS_RECEIPT_DATE = "use_business_date_as_receipt_trx_date";
    public static final String USE_BUSINESS_DATE_AS_TRANSACTION_DATE = "use_business_date_as_transaction_date";
    public static final String IS_REPOSSESSION_ENABLED = "is_repossession_enabled";
    public static final String SHOW_REPOSSESSION_AFTER_X_DPD = "show_repossession_after_x_dpd";
    public static final String RECEIPT_TRANSFER_MODE_READ_ONLY = "receipt_transfer_mode_read_only";
    public static final String FINOVA_CASH_MSG_FLOW_ID = "638b914a3ef07b5a0221ed82";
    public static final String FINOVA_CHEQUE_MSG_FLOW_ID = "638b9491907adf51817999c6";
    public static final String FINOVA_UPI_MSG_FLOW_ID = "638b94373ef07b5a0221ed83";
    public static final String MASKED_NUMBER_CONFIGURATION = "mask_all_customer_phone_numbers";
    public static final String PAISABUDDY_SMS_TEMPLATE_ID = "6555db03d6fc052d1f2aa712";
    public static final String CFL_SMS_TEMPLATE_ID = "1007096555731164733";
    public static final String CSL_TEMPLATE_ENCODED_MESSAGE = "%E0%A4%AA%E0%A5%8D%E0%A4%B0%E0%A4%BF%E0%A4%AF%20%E0%A4%97%E0%A5%8D%E0%A4%B0%E0%A4%BE%E0%A4%B9%E0%A4%95%20Vinay%2C%E0%A4%86%E0%A4%AA%E0%A4%95%E0%A5%87%20%E0%A4%A6%E0%A5%8D%E0%A4%B5%E0%A4%BE%E0%A4%B0%E0%A4%BE%20%E0%A4%B0%E0%A4%BE%E0%A4%B6%E0%A4%BF%20%E2%82%B9%20500%2F-%2C%E0%A4%AA%E0%A5%8D%E0%A4%B0%E0%A4%BE%E0%A4%AA%E0%A5%8D%E0%A4%A4%20%E0%A4%B9%E0%A5%81%E0%A4%88%20%E0%A4%B9%E0%A5%88%20%E0%A4%A6%E0%A4%BF%E0%A4%A8%E0%A4%BE%E0%A4%82%E0%A4%95%201-1-2032%20%E0%A4%95%E0%A5%8B%20%E0%A4%9C%E0%A4%BF%E0%A4%B8%E0%A4%95%E0%A5%80%20%E0%A4%96%E0%A4%BE%E0%A4%A4%E0%A4%BE%20%E0%A4%B8%E0%A4%82%E0%A4%95%E0%A5%8D%E0%A4%AF%E0%A4%BE%201234567%7D%20%E0%A4%B9%E0%A5%88%E0%A5%A4%20%E0%A4%86%E0%A4%AA%20%E0%A4%85%E0%A4%AA%E0%A4%A8%E0%A5%80%20%E0%A4%B0%E0%A4%B8%E0%A5%80%E0%A4%A6%20%E0%A4%87%E0%A4%B8%20%E0%A4%B2%E0%A4%BF%E0%A4%82%E0%A4%95%20%E0%A4%B8%E0%A5%87%20%E0%A4%AA%E0%A5%8D%E0%A4%B0%E0%A4%BE%E0%A4%AA%E0%A5%8D%E0%A4%A4%20%E0%A4%95%E0%A4%B0%20%E0%A4%B8%E0%A4%95%E0%A4%A4%E0%A5%87%20%E0%A4%B9%E0%A5%88%206778990000%E0%A5%A4%20%E0%A4%A7%E0%A4%A8%E0%A5%8D%E0%A4%AF%E0%A4%B5%E0%A4%BE%E0%A4%A6%20CSL%20Finance%20Limited";
    public static final String DECCAN_TEMPLATE_MESSAGE = "EMI amount received Rs {Var1} via {paymentMode} from loan number {Var2} please download receipt from below link {Var3} Thank you ! Deccan Finance";


    //Pagination Config
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public  static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
}
