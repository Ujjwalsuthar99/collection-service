package com.synoriq.synofin.collection.collectionservice.service.printService.implementation;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.synoriq.synofin.collection.collectionservice.rest.response.ThermalPrintDataDTO;
import com.synoriq.synofin.collection.collectionservice.service.printService.interfaces.PrintServiceInterface;
import org.springframework.stereotype.Service;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service("csl")
public class CslPrintDesign implements PrintServiceInterface {
    @Override
    public byte[] printServiceDesign(ThermalPrintDataDTO thermalPrintDataDTO) throws DocumentException, IOException {

        Rectangle thermalPaperSize = new Rectangle(288, 720);

        Document document = new Document(thermalPaperSize);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();
        BaseFont baseFont = BaseFont.createFont("Helvetica", BaseFont.WINANSI, BaseFont.EMBEDDED);
        Font headingFont = new Font(baseFont, 16, Font.BOLD);
        Font contentFont = new Font(baseFont, 12);

        // Add heading
        Paragraph heading1 = new Paragraph();
        heading1.setAlignment(Element.ALIGN_CENTER);
        heading1.setFont(headingFont);

        heading1.add("CSL Finance Limited\n");
        heading1.add("Receipt");
        document.add(heading1);


        // Add content
        Paragraph content = new Paragraph();
        content.setFont(contentFont);

        // Top Details
        Paragraph rightAligned1 = new Paragraph();
        rightAligned1.add(new Phrase("\n", contentFont));
        rightAligned1.add(new Phrase("Branch: " + thermalPrintDataDTO.getBranchName() + "\n", contentFont));
        rightAligned1.add(new Phrase("Date: " + thermalPrintDataDTO.getDateTime() + "\n", contentFont));
        rightAligned1.setAlignment(Element.ALIGN_RIGHT);
        content.add(rightAligned1);

        // BASELINE
        Paragraph baseLine = new Paragraph();
        baseLine.add(new Phrase("------------------------------------------------------"));
        baseLine.setAlignment(Element.ALIGN_CENTER);
        content.add(baseLine);

        // Receipt Details
        Paragraph rightAligned2 = new Paragraph();
        rightAligned2.add(new Phrase("Receipt No: " + thermalPrintDataDTO.getReceiptNo() + "\n", contentFont));
        rightAligned2.add(new Phrase("Customer Name: " + thermalPrintDataDTO.getCustomerName() + "\n", contentFont));
        rightAligned2.add(new Phrase("Mobile Number: " + thermalPrintDataDTO.getMobileNumber() + "\n", contentFont));
        rightAligned2.add(new Phrase("Collected From: " + thermalPrintDataDTO.getCollectedFrom() + "\n", contentFont));
        rightAligned2.setAlignment(Element.ALIGN_RIGHT);
        content.add(rightAligned2);

        // BASELINE
        content.add(baseLine);

        // Bank Related
        Paragraph rightAligned3 = new Paragraph();
        rightAligned2.add(new Phrase("Payment Mode: " + thermalPrintDataDTO.getPaymentMode() + "\n", contentFont));
        rightAligned3.add(new Phrase("Cheque No: " + thermalPrintDataDTO.getChequeNo() + "\n", contentFont));
        rightAligned3.add(new Phrase("Bank Name: " + thermalPrintDataDTO.getBankName() + "\n", contentFont));
        rightAligned3.add(new Phrase("IFSC Code: " + thermalPrintDataDTO.getIfsc() + "\n", contentFont));
        rightAligned3.add(new Phrase("Account No: " + thermalPrintDataDTO.getBankAccountNumber() + "\n", contentFont));
        rightAligned3.add(new Phrase("Transaction Reference: " + thermalPrintDataDTO.getTransactionNumber() + "\n", contentFont));
        rightAligned3.setAlignment(Element.ALIGN_RIGHT);
        content.add(rightAligned3);

        // BASELINE
        content.add(baseLine);

        Paragraph loanNumber = new Paragraph();
        loanNumber.add(new Phrase("Loan ID: " + thermalPrintDataDTO.getLoanNumber() + "\n", new Font(baseFont, 11)));
        loanNumber.setAlignment(Element.ALIGN_RIGHT);
        content.add(loanNumber);

        // BASELINE
        content.add(baseLine);

        // User Details
        Paragraph rightAligned4 = new Paragraph();
        rightAligned4.add(new Phrase("User Name: " + thermalPrintDataDTO.getUserName() + "\n", contentFont));
        rightAligned4.add(new Phrase("User Code: " + thermalPrintDataDTO.getUserCode() + "\n", contentFont));
        rightAligned4.setAlignment(Element.ALIGN_RIGHT);
        content.add(rightAligned4);

        // BASELINE
        content.add(baseLine);

        // Loan Details
        Paragraph rightAligned5 = new Paragraph();
        rightAligned5.add(new Phrase("Actual Emi: " + thermalPrintDataDTO.getActualEmi() + "\n", contentFont));
        rightAligned5.add(new Phrase("Receipt Amount: " + thermalPrintDataDTO.getReceiptAmount() + "\n", contentFont));
        rightAligned5.setAlignment(Element.ALIGN_RIGHT);
        content.add(rightAligned5);

        // BASELINE
        content.add(baseLine);

        // Total Amount
        Paragraph rightAligned6 = new Paragraph();
        rightAligned6.add(new Phrase("Total: " + thermalPrintDataDTO.getTotal() + ".00 ₹" + "\n", new Font(baseFont, 14)));
        rightAligned6.setAlignment(Element.ALIGN_RIGHT);
        content.add(rightAligned6);

        // BASELINE
        content.add(baseLine);

        Paragraph text = new Paragraph();
        text.add(new Phrase("This is a computer generated receipt and the above payment is subject to clearance from the accounts department of CSL Finance.", contentFont));
        text.setAlignment(Element.ALIGN_CENTER);
        content.add(text);

        // BASELINE
        content.add(baseLine);

        Paragraph address = new Paragraph();
        address.add(new Phrase("Corp off.716-717, 7th Floor, Tower-B, World Trade Tower, Sector-16, Noida, U.P.-201301 Toll Free No:1800-102-9925", contentFont));
        address.setAlignment(Element.ALIGN_CENTER);
        content.add(address);

        document.add(content);

        // Close the document
        document.close();

        return byteArrayOutputStream.toByteArray();


    }
}
