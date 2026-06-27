package com.proofit.proofit.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.proofit.proofit.model.Document;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class CertificateService {

    public byte[] generateCertificate(Document document) throws Exception {
        com.itextpdf.text.Document pdf = new com.itextpdf.text.Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(pdf, out);
        pdf.open();

        BaseColor purple = new BaseColor(99, 102, 241);
        BaseColor dark = new BaseColor(15, 15, 15);
        BaseColor gray = new BaseColor(100, 100, 100);
        BaseColor green = new BaseColor(34, 197, 94);

        PdfContentByte canvas = writer.getDirectContentUnder();
        canvas.setColorFill(dark);
        canvas.rectangle(0, 0, PageSize.A4.getWidth(), PageSize.A4.getHeight());
        canvas.fill();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, BaseColor.WHITE);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, gray);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, purple);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.WHITE);
        Font hashFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, gray);
        Font verifiedFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, green);

        canvas.setColorFill(purple);
        canvas.rectangle(0, PageSize.A4.getHeight() - 8, PageSize.A4.getWidth(), 8);
        canvas.fill();

        pdf.add(Chunk.NEWLINE);
        pdf.add(Chunk.NEWLINE);

        Paragraph logo = new Paragraph("ProofIt - Certificate of Originality", titleFont);
        logo.setAlignment(Element.ALIGN_CENTER);
        pdf.add(logo);

        Paragraph tagline = new Paragraph("Tamper-Proof Document Timestamping", subtitleFont);
        tagline.setAlignment(Element.ALIGN_CENTER);
        tagline.setSpacingBefore(8);
        pdf.add(tagline);

        pdf.add(Chunk.NEWLINE);
        LineSeparator line = new LineSeparator(1, 80, purple, Element.ALIGN_CENTER, -5);
        pdf.add(new Chunk(line));
        pdf.add(Chunk.NEWLINE);

        Paragraph verified = new Paragraph("DOCUMENT VERIFIED AND TIMESTAMPED", verifiedFont);
        verified.setAlignment(Element.ALIGN_CENTER);
        verified.setSpacingBefore(10);
        pdf.add(verified);
        pdf.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(80);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidths(new float[]{35, 65});
        table.setSpacingBefore(20);

        addTableRow(table, "File Name", document.getFileName(), labelFont, valueFont);
        addTableRow(table, "Owner", document.getUser().getName(), labelFont, valueFont);
        addTableRow(table, "Email", document.getUser().getEmail(), labelFont, valueFont);
        addTableRow(table, "Timestamped On", document.getUploadedAt().toString(), labelFont, valueFont);
        addTableRow(table, "File Size", formatSize(document.getFileSize()), labelFont, valueFont);
        pdf.add(table);

        pdf.add(Chunk.NEWLINE);

        PdfPTable hashTable = new PdfPTable(1);
        hashTable.setWidthPercentage(80);
        hashTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        PdfPCell hashTitle = new PdfPCell(new Phrase("SHA-256 CRYPTOGRAPHIC FINGERPRINT", labelFont));
        hashTitle.setBackgroundColor(new BaseColor(20, 20, 40));
        hashTitle.setBorderColor(purple);
        hashTitle.setPadding(10);
        hashTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        hashTable.addCell(hashTitle);

        PdfPCell hashValue = new PdfPCell(new Phrase(document.getDocumentHash(), hashFont));
        hashValue.setBackgroundColor(new BaseColor(10, 10, 20));
        hashValue.setBorderColor(purple);
        hashValue.setPadding(10);
        hashTable.addCell(hashValue);

        PdfPCell chainTitle = new PdfPCell(new Phrase("CHAIN HASH", labelFont));
        chainTitle.setBackgroundColor(new BaseColor(20, 20, 40));
        chainTitle.setBorderColor(purple);
        chainTitle.setPadding(10);
        chainTitle.setHorizontalAlignment(Element.ALIGN_CENTER);
        hashTable.addCell(chainTitle);

        PdfPCell chainValue = new PdfPCell(new Phrase(document.getChainHash(), hashFont));
        chainValue.setBackgroundColor(new BaseColor(10, 10, 20));
        chainValue.setBorderColor(purple);
        chainValue.setPadding(10);
        hashTable.addCell(chainValue);

        pdf.add(hashTable);
        pdf.add(Chunk.NEWLINE);

        Font linkFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, purple);
        Paragraph verifyLink = new Paragraph(
                "Verify at: https://proofit.onrender.com/frontend/verify.html?token=" +
                        document.getVerificationToken(), linkFont);
        verifyLink.setAlignment(Element.ALIGN_CENTER);
        pdf.add(verifyLink);

        pdf.add(Chunk.NEWLINE);
        LineSeparator bottomLine = new LineSeparator(1, 80, purple, Element.ALIGN_CENTER, -5);
        pdf.add(new Chunk(bottomLine));

        Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, gray);
        Paragraph footer = new Paragraph(
                "Generated by ProofIt using SHA-256 hash chaining. " +
                        "Any modification to the original file will invalidate this certificate.", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(10);
        pdf.add(footer);

        pdf.close();
        return out.toByteArray();
    }

    private void addTableRow(PdfPTable table, String label, String value,
                             Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(20, 20, 40));
        labelCell.setBorderColor(new BaseColor(40, 40, 60));
        labelCell.setPadding(10);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBackgroundColor(new BaseColor(15, 15, 30));
        valueCell.setBorderColor(new BaseColor(40, 40, 60));
        valueCell.setPadding(10);
        table.addCell(valueCell);
    }

    private String formatSize(Long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }
}