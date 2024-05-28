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

    private String vendor;
    private ReceiptServiceDtoRequest receiptBody;

}
