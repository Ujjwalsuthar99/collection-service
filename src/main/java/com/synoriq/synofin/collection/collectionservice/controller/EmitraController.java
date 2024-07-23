package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.rest.request.emitraRequestDTOs.*;
import com.synoriq.synofin.collection.collectionservice.service.EmitraService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/v1/emitra/")
public class EmitraController {


    public EmitraController(EmitraService emitraService) {
        this.emitraService = emitraService;
    }
    private final EmitraService emitraService;


    @PostMapping("verify-sso-token")
    public ResponseEntity<Object> verifySsoToken(@RequestHeader("Authorization") String token, @RequestParam("loanId") Long loanId, @RequestBody VerifySsoTokenDTO requestBody) throws Exception {

        Object result = emitraService.verifySsoToken(token, loanId, requestBody);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("service-transaction")
    public ResponseEntity<Object> serviceTransaction(@RequestHeader("Authorization") String token, @RequestParam("loanId") Long loanId, @RequestBody ServiceTransactionDTO requestBody) throws Exception {

        Object result = emitraService.serviceTransaction(token, loanId, requestBody);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("update-transaction-posting")
    public ResponseEntity<Object> updateTransactionPosting(@RequestHeader("Authorization") String token, @RequestParam("loanId") Long loanId, @RequestBody UpdateTransactionPostingDTO requestBody) throws Exception {

        Object result = emitraService.updateTransactionPosting(token, loanId, requestBody);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("verify-transaction")
    public ResponseEntity<Object> verifyTransaction(@RequestHeader("Authorization") String token, @RequestParam("loanId") Long loanId, @RequestBody VerifyTransactionDTO requestBody) throws Exception {

        Object result = emitraService.verifyTransaction(token, loanId, requestBody);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("get-kiosk-details")
    public ResponseEntity<Object> getKioskDetails(@RequestHeader("Authorization") String token, @RequestParam("loanId") Long loanId, @RequestBody GetKioskDetailsDTO requestBody) throws Exception {

        Object result = emitraService.getKioskDetails(token, loanId, requestBody);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
