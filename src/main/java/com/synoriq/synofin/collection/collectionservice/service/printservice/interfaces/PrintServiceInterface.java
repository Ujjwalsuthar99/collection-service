package com.synoriq.synofin.collection.collectionservice.service.printservice.interfaces;

import com.itextpdf.text.DocumentException;
import com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos.ThermalPrintDataDTO;

import java.io.IOException;

public interface PrintServiceInterface {

    public byte[] printServiceDesign(ThermalPrintDataDTO thermalPrintDataDTO) throws DocumentException, IOException;
}
