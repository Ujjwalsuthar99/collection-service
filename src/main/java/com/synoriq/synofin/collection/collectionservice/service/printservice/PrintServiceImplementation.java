package com.synoriq.synofin.collection.collectionservice.service.printservice;

import com.itextpdf.text.DocumentException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos.ThermalPrintDataDTO;
import com.synoriq.synofin.collection.collectionservice.service.printservice.interfaces.PrintServiceInterface;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PrintServiceImplementation {

    @Autowired
    BeanFactory bean;

    public byte[] printDesign(ThermalPrintDataDTO thermalPrintDataDTO, String clientId) throws CustomException {
        PrintServiceInterface printServiceInterface = bean.getBean(clientId, PrintServiceInterface.class);
        try {
            return printServiceInterface.printServiceDesign(thermalPrintDataDTO);
        } catch (DocumentException | IOException e) {
            throw new CustomException(e.getMessage());
        }
    }
}
