package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReportGenerator {
    public void generateReport(Map<String, DocumentProcessor.PaymentSummary> paymentSummary) throws IOException {
        List<String> medios = Arrays.asList("PAT", "PAC", "");

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Reporte de Cuadratura</title>")
                .append("<style>table {border-collapse: collapse; width: 100%;} ")
                .append("th, td {border: 1px solid black; padding: 8px; text-align: left;}</style></head><body>")
                .append("<h1>Reporte de Cuadratura</h1><table>")
                .append("<tr><th></th>");

        for (String medio : medios) {
            String nombreColumna = medio.isEmpty() ? "Sin Medio" : medio;
            html.append("<th>").append(nombreColumna).append("</th>");
        }
        html.append("<th>Totales</th></tr>");
        html.append("<tr><td>Cantidad Docs</td>");

        int totalDocs = 0;
        for (String medio : medios) {
            DocumentProcessor.PaymentSummary summary = paymentSummary.get(medio);
            int count = (summary != null) ? summary.documentCount : 0;
            html.append("<td>").append(count).append("</td>");
            totalDocs += count;
        }
        html.append("<td>").append(totalDocs).append("</td></tr>");
        html.append("<tr><td>Total a Pagar</td>");
        double totalAmount = 0;
        for (String medio : medios) {
            DocumentProcessor.PaymentSummary summary = paymentSummary.get(medio);
            double total = (summary != null) ? summary.totalAmount : 0.0;
            html.append("<td>").append(String.format("%.2f", total)).append("</td>");
            totalAmount += total;
        }
        html.append("<td>").append(String.format("%.2f", totalAmount)).append("</td></tr>");
        html.append("</table></body></html>");

        Files.createDirectories(Paths.get("output"));
        Files.write(Paths.get("output/report.html"), html.toString().getBytes());
    }
}
