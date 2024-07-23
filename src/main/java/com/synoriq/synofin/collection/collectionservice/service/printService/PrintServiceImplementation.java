package com.synoriq.synofin.collection.collectionservice.service.printService;

import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.ThermalPrintDataDTO;
import com.synoriq.synofin.collection.collectionservice.service.printService.interfaces.PrintServiceInterface;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrintServiceImplementation {

    @Autowired
    BeanFactory bean;

    public byte[] printDesign(ThermalPrintDataDTO thermalPrintDataDTO, String clientId) throws Exception {
        PrintServiceInterface printServiceInterface = bean.getBean(clientId, PrintServiceInterface.class);
        return printServiceInterface.printServiceDesign(thermalPrintDataDTO);
    }
}
