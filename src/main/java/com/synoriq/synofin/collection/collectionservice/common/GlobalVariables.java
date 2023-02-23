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
    public static final String CASH_COLLECTION_DEFAULT_LIMIT = "cash_collection_default_limit";
    public static final String USE_BUSINESS_DATE_AS_RECEIPT_DATE = "use_business_date_as_receipt_trx_date";


    //Pagination Config
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public  static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
}
