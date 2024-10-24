package com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos;

import com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos.ReceiptServiceDtoRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkCollectionRequestDTO {

    private ReceiptServiceDtoRequest receiptBody;
    private String mobileNumber;
    private String customerName;

}
