package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class DocumentProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XMLGenerator xmlGenerator = new XMLGenerator();
    private final ReportGenerator reportGenerator = new ReportGenerator();

    public void processDocuments(String jsonContent) throws IOException {
        JsonNode rootNode = objectMapper.readTree(jsonContent);
        JsonNode documentsNode = rootNode.get("documents");

        List<Document> documentos = new ArrayList<>();
        Map<String, PaymentSummary> summary = new HashMap<>();

        for (JsonNode docNode : documentsNode) {
            String base64Content = docNode.get("ContentBase64").asText();
            String jsonDocument = decompressBase64Gzip(base64Content);

            Document documento = objectMapper.readValue(jsonDocument, Document.class);
            documentos.add(documento);

            // Actualizar resumen por medio de pago
            summary.computeIfAbsent(documento.getMedioPago(), k -> new PaymentSummary())
                    .addPayment(documento.getTotalAPagar());

            // Generar XML
            xmlGenerator.generateXML(documento);
        }

        // Generar reporte
        reportGenerator.generateReport(summary);
    }


    String decompressBase64Gzip(String base64) throws IOException {
        byte[] gzipData = Base64.getDecoder().decode(base64);
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(gzipData));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }

            return baos.toString("UTF-8");
        }
    }

     static class PaymentSummary {
        int documentCount;
        double totalAmount;

        public void addPayment(double amount) {
            documentCount++;
            totalAmount += amount;
        }
    }
}
