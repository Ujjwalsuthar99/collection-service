package com.synoriq.synofin.collection.collectionservice.service.factory;

import com.synoriq.synofin.collection.collectionservice.implementation.PaymentLinkServiceImpl;
import com.synoriq.synofin.collection.collectionservice.implementation.QrCodeServiceImpl;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeStatusCheckRequestDTO;
import com.synoriq.synofin.collection.collectionservice.service.DigitalTransactionChecker;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatusCheckerFactory {

    private final QrCodeServiceImpl qrCodeService;
    private final PaymentLinkServiceImpl paymentLinkService;

    public TransactionStatusCheckerFactory(QrCodeServiceImpl qrCodeService, PaymentLinkServiceImpl paymentLinkService) {
        this.qrCodeService = qrCodeService;
        this.paymentLinkService = paymentLinkService;
    }

    public DigitalTransactionChecker getChecker(Object requestBody) {
        if (requestBody instanceof DynamicQrCodeStatusCheckRequestDTO) {
            return qrCodeService;
        } else {
            return paymentLinkService;
        }
    }
}