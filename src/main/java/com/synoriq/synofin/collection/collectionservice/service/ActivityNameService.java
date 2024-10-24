package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityEvent.*;

@Service
@Slf4j
public class ActivityNameService {
    public Object getActivityDetails() throws CollectionException {
        Map<String, String> responseActivity = new LinkedHashMap<>();
        BaseDTOResponse<Object> baseDTOResponse  = null;
        try {
            responseActivity.put("login", LOGIN);
            responseActivity.put("app_access", APP_ACCESS);
            responseActivity.put("create_receipt", CREATE_RECEIPT);
            responseActivity.put("create_followup", CREATE_FOLLOWUP);
            responseActivity.put("add_contact", ADD_ADDITIONAL_CONTACT);
            responseActivity.put("receipt_transfer", RECEIPT_TRANSFER);
            responseActivity.put("receipt_transfer_approve", RECEIPT_TRANSFER_APPROVE);
            responseActivity.put("receipt_transfer_reject", RECEIPT_TRANSFER_REJECT);
            responseActivity.put("receipt_transfer_cancel", RECEIPT_TRANSFER_CANCEL);
            responseActivity.put("pending", RECEIPT_TRANSFER_PENDING);
            responseActivity.put("logout", LOGOUT);
            baseDTOResponse = new BaseDTOResponse<>(responseActivity);
            return baseDTOResponse;
        } catch(Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(1017002);
            throw new CollectionException(errCode, 1017002);
        }

    }
}
