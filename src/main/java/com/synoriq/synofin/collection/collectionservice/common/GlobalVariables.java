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
}
