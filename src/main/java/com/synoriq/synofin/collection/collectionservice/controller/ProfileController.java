package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
    @GetMapping(value = "users/profile")
    public ResponseEntity<Object> getProfileDetails(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "username") String username) {

        Object profileDetailResponse = profileService.getProfileDetails(bearerToken, username);
        return new ResponseEntity<>(profileDetailResponse, HttpStatus.OK);

    }
}

