package com.synoriq.synofin.collection.collectionservice.rest.request.paymentLinkDTOs;

import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
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
