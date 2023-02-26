package com.synoriq.synofin.collection.collectionservice.service;
import com.synoriq.synofin.collection.collectionservice.common.ActivityEvent;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ActivityNameService {
    public Object getActivityDetails() throws Exception {
        Map<String, String> responseActivity = new LinkedHashMap<>();
        BaseDTOResponse<Object> baseDTOResponse  = null;
        try {
            responseActivity.put("login", ActivityEvent.LOGIN);
            responseActivity.put("app_access", ActivityEvent.APP_ACCESS);
            responseActivity.put("create_receipt", ActivityEvent.CREATE_RECEIPT);
            responseActivity.put("create_followup", ActivityEvent.CREATE_FOLLOWUP);
            responseActivity.put("add_contact", ActivityEvent.ADD_ADDITIONAL_CONTACT);
            responseActivity.put("receipt_transfer", ActivityEvent.RECEIPT_TRANSFER);
            responseActivity.put("receipt_transfer_approve", ActivityEvent.RECEIPT_TRANSFER_APPROVE);
            responseActivity.put("receipt_transfer_reject", ActivityEvent.RECEIPT_TRANSFER_REJECT);
            responseActivity.put("receipt_transfer_cancel", ActivityEvent.RECEIPT_TRANSFER_CANCEL);
            responseActivity.put("pending", ActivityEvent.RECEIPT_TRANSFER_PENDING);
            responseActivity.put("logout", ActivityEvent.LOGOUT);
            baseDTOResponse = new BaseDTOResponse<>(responseActivity);
            return baseDTOResponse;
        } catch(Exception e) {
            throw new Exception("1017002");
        }

    }
}
