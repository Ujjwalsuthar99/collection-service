package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface TaskService {

    public BaseDTOResponse<Object> getTaskDetails(Long userId, Integer pageNo, Integer pageSize) throws Exception;
    public Object getTaskDetailByLoanId(String token, TaskDetailRequestDTO taskDetailRequestDTO) throws Exception;
    public BaseDTOResponse<Object> getTaskDetailsBySearchKey(Long userId, String searchKey, Integer pageNo, Integer pageSize) throws Exception;
    public BaseDTOResponse<Object> getLoanIdsByLoanId(Long loanId) throws Exception;


}
