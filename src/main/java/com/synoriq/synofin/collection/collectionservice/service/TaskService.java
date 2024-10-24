package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskFilterRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface TaskService {

     BaseDTOResponse<Object> getTaskDetails(Long userId, Integer pageNo, Integer pageSize, TaskFilterRequestDTO taskFilterRequestDTO) throws CollectionException;
     Object getTaskDetailByLoanId(String token, TaskDetailRequestDTO taskDetailRequestDTO) throws CollectionException;
     BaseDTOResponse<Object> getTaskDetailsBySearchKey(Long userId, String searchKey, Integer pageNo, Integer pageSize) throws CollectionException;
     BaseDTOResponse<Object> getLoanIdsByLoanId(Long loanId) throws CollectionException;


}
