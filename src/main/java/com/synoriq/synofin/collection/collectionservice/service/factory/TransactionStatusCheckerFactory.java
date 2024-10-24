package com.synoriq.synofin.collection.collectionservice.service.factory;

import com.synoriq.synofin.collection.collectionservice.service.DigitalTransactionChecker;
import com.synoriq.synofin.collection.collectionservice.service.implementation.PaymentLinkServiceImpl;
import com.synoriq.synofin.collection.collectionservice.service.implementation.QrCodeServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatusCheckerFactory {

    private final QrCodeServiceImpl qrCodeService;
    private final PaymentLinkServiceImpl paymentLinkService;

    public TransactionStatusCheckerFactory(QrCodeServiceImpl qrCodeService, PaymentLinkServiceImpl paymentLinkService) {
        this.qrCodeService = qrCodeService;
        this.paymentLinkService = paymentLinkService;
    }

    public DigitalTransactionChecker getChecker(String serviceName) {
        if (serviceName.equals("dynamic_qr_code")) {
            return qrCodeService;
        }
        return paymentLinkService;
    }
}