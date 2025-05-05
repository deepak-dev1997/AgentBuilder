package com.agentbuilder.Utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public final class DocumentTextExtractor {

    private DocumentTextExtractor() {
        // utility class
    }

    /**
     * Extracts plain text from a PDF, DOC or DOCX file.
     *
     * @param file the uploaded multipart file
     * @return the extracted text
     * @throws IOException if parsing fails or file type unsupported
     */
    public static String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File name is missing");
        }

        String text;
        String ext = getExtension(filename).toLowerCase();

        try (InputStream in = file.getInputStream()) {
            switch (ext) {
                case "pdf":
                    text = extractFromPdf(in);
                    break;
                case "PDF":
                    text = extractFromPdf(in);
                    break;
                case "docx":
                    text = extractFromDocx(in);
                    break;
                case "doc":
                    text = extractFromDoc(in);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file type: " + ext);
            }
        }

        return text;
    }

    private static String extractFromPdf(InputStream in) throws IOException {
        try (PDDocument document = PDDocument.load(in)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String extractFromDocx(InputStream in) throws IOException {
        try (XWPFDocument docx = new XWPFDocument(in);
             XWPFWordExtractor extractor = new XWPFWordExtractor(docx)) {
            return extractor.getText();
        }
    }

    private static String extractFromDoc(InputStream in) throws IOException {
        try (HWPFDocument doc = new HWPFDocument(in);
             WordExtractor extractor = new WordExtractor(doc)) {
            return String.join("\n", extractor.getParagraphText());
        }
    }

    private static String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx == -1 || idx == filename.length() - 1) {
            return "";
        }
        return filename.substring(idx + 1);
    }
}