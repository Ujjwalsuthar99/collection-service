package com.synoriq.synofin.collection.collectionservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface PaymentLinkService {

    Object sendPaymentLink(String token, Object data, MultipartFile paymentReferenceImage, MultipartFile selfieImage) throws Exception;

}
