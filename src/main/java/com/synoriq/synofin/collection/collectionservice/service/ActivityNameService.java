package com.synoriq.synofin.collection.collectionservice.service;
import com.synoriq.synofin.collection.collectionservice.common.ActivityEvent;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ActivityNameService {
    public Object getActivityDetails() throws Exception {
        Map<String, String> responseActivity = new HashMap<>();
        BaseDTOResponse<Object> baseDTOResponse  = null;
        try {
            responseActivity.put("create_receipt", ActivityEvent.CREATE_RECEIPT);
            responseActivity.put("create_followup", ActivityEvent.CREATE_FOLLOWUP);
            responseActivity.put("add_contact", ActivityEvent.ADD_CONTACT);
            responseActivity.put("receipt_transfer", ActivityEvent.RECEIPT_TRANSFER);
            responseActivity.put("closed", ActivityEvent.CLOSED);
            responseActivity.put("login", ActivityEvent.LOGIN);
            responseActivity.put("logout", ActivityEvent.LOGOUT);
            responseActivity.put("reschedule", ActivityEvent.RESCHEDULE);
            responseActivity.put("change_password", ActivityEvent.CHANGE_PASSWORD);





            baseDTOResponse = new BaseDTOResponse<>(responseActivity);
            return baseDTOResponse;
        } catch(Exception e) {
            throw new Exception("1017002");
        }

    }
}
