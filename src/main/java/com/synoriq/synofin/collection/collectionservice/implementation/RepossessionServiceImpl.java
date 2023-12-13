package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RepossessionEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.RepossessionRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs.RepossessionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.RepossessionDTOs.RepossessionCommonDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.RepossessionDTOs.RepossessionResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.RepossessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class RepossessionServiceImpl implements RepossessionService {

    @Autowired
    private RepossessionRepository repossessionRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Override
    public BaseDTOResponse<Object> getRepossessionData(Long loanId) throws Exception {
        Optional<RepossessionEntity> repossessionEntity = Optional.of(new RepossessionEntity());
        List<Long> referenceIds = new ArrayList<>();
        RepossessionResponseDTO repossessionResponseDTO = new RepossessionResponseDTO();
        boolean isHistory = false;
        List<RepossessionCommonDTO> current = new ArrayList<>();
        List<RepossessionCommonDTO> history = new ArrayList<>();
        try {

            List<CollectionActivityLogsEntity> collectionActivityLogsEntityList = collectionActivityLogsRepository.getActivityLogsDataByLoanIdWithRepossession(loanId);
            if (collectionActivityLogsEntityList.size() > 0) {
                for (CollectionActivityLogsEntity collectionActivityLogsEntity : collectionActivityLogsEntityList) {
                    RepossessionCommonDTO repossessionCommonDTO = new RepossessionCommonDTO();
                    if (!referenceIds.contains(collectionActivityLogsEntity.getReferenceId())) {
                        repossessionEntity = repossessionRepository.findById(collectionActivityLogsEntity.getReferenceId());
                        isHistory = referenceIds.size() > 0;
                    }
                    if (repossessionEntity.isPresent()) {
                        Map<String, Object> yardJson = new ObjectMapper().convertValue(repossessionEntity.get().getYardDetailsJson(), Map.class);
                        Map<String, Object> remarksJson = new ObjectMapper().convertValue(repossessionEntity.get().getRemarks(), Map.class);

                        repossessionCommonDTO.setStatus(repossessionEntity.get().getStatus());
                        repossessionCommonDTO.setAgency(repossessionEntity.get().getRecoveryAgency());
                        repossessionCommonDTO.setAttachments(collectionActivityLogsEntity.getImages());
                        repossessionCommonDTO.setRemark(String.valueOf(remarksJson.get(collectionActivityLogsEntity.getActivityName().substring(13)+"_remarks")));
                        repossessionCommonDTO.setActionBy(repossessionRepository.getNameFromUsers(collectionActivityLogsEntity.getActivityBy()));
                        repossessionCommonDTO.setApprovedBy(repossessionRepository.getNameFromUsers(collectionActivityLogsEntity.getActivityBy()));
                        repossessionCommonDTO.setAssignTo(repossessionEntity.get().getAssignedTo() != null ? repossessionRepository.getNameFromUsers(repossessionEntity.get().getAssignedTo()) : null);
                        repossessionCommonDTO.setCreateDate(collectionActivityLogsEntity.getActivityDate());
                        repossessionCommonDTO.setInitiatedBy(repossessionRepository.getNameFromUsers(repossessionEntity.get().getCreatedBy()));
                        repossessionCommonDTO.setYardContactNumber(repossessionEntity.get().getYardDetailsJson() != null ? String.valueOf(yardJson.get("yard_contact_number")) : null);
                        repossessionCommonDTO.setYardName(repossessionEntity.get().getYardDetailsJson() != null ? String.valueOf(yardJson.get("yard_name")) : null);
                        repossessionCommonDTO.setVehicleHandoverTo(repossessionEntity.get().getYardDetailsJson() != null ? String.valueOf(yardJson.get("vehicle_handover_to")) : null);

                        if (isHistory) {
                            history.add(repossessionCommonDTO);
                        } else {
                            current.add(repossessionCommonDTO);
                        }
                    }
                    referenceIds.add(collectionActivityLogsEntity.getReferenceId());
                }
                repossessionResponseDTO.setCurrent(current);
                repossessionResponseDTO.setHistory(history);
            }

            return new BaseDTOResponse<>(repossessionResponseDTO);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public BaseDTOResponse<Object> initiateRepossession(String token, RepossessionRequestDTO requestDto) throws Exception {
        CollectionActivityLogDTO collectionActivityLogDTO = new CollectionActivityLogDTO();
        RepossessionEntity repossessionEntity = new RepossessionEntity();
        try {
            Map<String, Object> remarksJson = new HashMap<>();
            remarksJson.put("initiated_remarks", requestDto.getRemarks());

            repossessionEntity.setRemarks(remarksJson);
            repossessionEntity.setLoanId(requestDto.getLoanId());
            repossessionEntity.setDeleted(false);
            repossessionEntity.setCreatedDate(new Date());
            repossessionEntity.setCreatedBy(requestDto.getInitiatedBy());
            repossessionEntity.setStatus("initiated");
            repossessionRepository.save(repossessionEntity);

            // created activity for repossession initiate
            collectionActivityLogDTO.setActivityName("repossession_initiated");
            collectionActivityLogDTO.setAddress("{}");
            collectionActivityLogDTO.setBatteryPercentage(requestDto.getBatteryPercentage());
            collectionActivityLogDTO.setImages(requestDto.getAttachments());
            collectionActivityLogDTO.setDeleted(false);
            collectionActivityLogDTO.setRemarks("Repossession initiated against loan id" + requestDto.getLoanId());
            collectionActivityLogDTO.setGeolocationData(requestDto.getGeoLocationData());
            collectionActivityLogDTO.setLoanId(requestDto.getLoanId());
            collectionActivityLogDTO.setUserId(requestDto.getInitiatedBy());
            collectionActivityLogDTO.setDistanceFromUserBranch(0D);
            Long activityId = activityLogService.createActivityLogs(collectionActivityLogDTO, token);
            BaseDTOResponse<Object> resp = activityLogService.getActivityLogsById(activityId);

            CollectionActivityLogsEntity collectionActivityLogsEntity = new ObjectMapper().convertValue(resp.getData(), CollectionActivityLogsEntity.class);
            collectionActivityLogsEntity.setReferenceId(repossessionEntity.getRepossessionId());
            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return new BaseDTOResponse<>(repossessionEntity);
    }

    @Override
    public BaseDTOResponse<Object> yardRepossession(String token, RepossessionRequestDTO requestDto) throws Exception {
        CollectionActivityLogDTO collectionActivityLogDTO = new CollectionActivityLogDTO();
        RepossessionEntity repossessionEntity;
        try {
            repossessionEntity = repossessionRepository.getById(requestDto.getRepoId());
            Map<String, Object> remarksJson = new ObjectMapper().convertValue(repossessionEntity.getRemarks(), Map.class);
            remarksJson.put(requestDto.getStatus()+"_remarks", requestDto.getRemarks());
            repossessionEntity.setRemarks(remarksJson);
            repossessionEntity.setYardDetailsJson(requestDto.getYardDetailsJson());
            repossessionRepository.save(repossessionEntity);

            // created activity for repossession flow
            collectionActivityLogDTO.setActivityName("repossession_" + requestDto.getStatus());
            collectionActivityLogDTO.setAddress("{}");
            collectionActivityLogDTO.setBatteryPercentage(requestDto.getBatteryPercentage());
            collectionActivityLogDTO.setImages(requestDto.getAttachments());
            collectionActivityLogDTO.setDeleted(false);
            collectionActivityLogDTO.setRemarks("Repossession " + StringUtils.capitalize(requestDto.getStatus()) + " against loan id" + requestDto.getLoanId());
            collectionActivityLogDTO.setGeolocationData(requestDto.getGeoLocationData());
            collectionActivityLogDTO.setLoanId(requestDto.getLoanId());
            collectionActivityLogDTO.setUserId(requestDto.getInitiatedBy());
            collectionActivityLogDTO.setDistanceFromUserBranch(0D);
            Long activityId = activityLogService.createActivityLogs(collectionActivityLogDTO, token);
            BaseDTOResponse<Object> resp = activityLogService.getActivityLogsById(activityId);

            CollectionActivityLogsEntity collectionActivityLogsEntity = new ObjectMapper().convertValue(resp.getData(), CollectionActivityLogsEntity.class);
            collectionActivityLogsEntity.setReferenceId(repossessionEntity.getRepossessionId());
            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return new BaseDTOResponse<>(repossessionEntity);
    }

}


