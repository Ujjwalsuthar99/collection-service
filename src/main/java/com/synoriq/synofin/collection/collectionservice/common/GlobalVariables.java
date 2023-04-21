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
    public static final String ONLINE_COLLECTION_DEFAULT_LIMIT = "online_collection_default_limit";
    public static final String CASH_COLLECTION_DEFAULT_LIMIT = "cash_collection_default_limit";
    public static final String SHORTEN_URL_UAT = "http://3.111.153.160:7011/integration/v1/url/shortan";
    public static final String FINOVA_MSG_API_URL = "https://api.msg91.com/api/v5/flow/";
    public static final String USE_BUSINESS_DATE_AS_RECEIPT_DATE = "use_business_date_as_receipt_trx_date";
    public static final String USE_BUSINESS_DATE_AS_TRANSACTION_DATE = "use_business_date_as_transaction_date";
    public static final String FINOVA_CASH_MSG_FLOW_ID = "638b914a3ef07b5a0221ed82";
    public static final String FINOVA_CHEQUE_MSG_FLOW_ID = "638b9491907adf51817999c6";
    public static final String FINOVA_UPI_MSG_FLOW_ID = "638b94373ef07b5a0221ed83";
    public static final String MASKED_NUMBER_CONFIGURATION = "mask_all_customer_phone_numbers";


    //Pagination Config
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public  static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
}
