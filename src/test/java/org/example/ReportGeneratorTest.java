package org.example;

import org.example.DocumentProcessor.PaymentSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private static final String TEST_OUTPUT_DIR = "test_output";
    private static final String REPORT_FILE = "report.html";

    @Before
    public void setUp() {
        reportGenerator = new ReportGenerator();
        try {
            Files.createDirectories(Paths.get(TEST_OUTPUT_DIR));
        } catch (IOException e) {
            fail("No se pudo crear el directorio de prueba: " + e.getMessage());
        }
    }

    @Test
    public void testGenerateReport_WithAllPaymentMethods() throws IOException {
        Map<String, PaymentSummary> summary = createTestSummary();
        reportGenerator.generateReport(summary);
        Path reportPath = Paths.get("output", REPORT_FILE);
        assertTrue("El reporte HTML debe existir", Files.exists(reportPath));

        String htmlContent = new String(Files.readAllBytes(reportPath));

        assertTrue(htmlContent.contains("<h1>Reporte de Cuadratura</h1>"));
        assertTrue(htmlContent.contains("<table>"));

        assertTrue(htmlContent.contains("<td>5</td>")); // PAT count
        assertTrue(htmlContent.contains("<td>12500,75</td>")); // PAT amount
        assertTrue(htmlContent.contains("<td>3</td>")); // PAC count
        assertTrue(htmlContent.contains("<td>7800,50</td>")); // PAC amount
        assertTrue(htmlContent.contains("<td>2</td>")); // Empty count
        assertTrue(htmlContent.contains("<td>3200,25</td>")); // Empty amount
        assertTrue(htmlContent.contains("<td>10</td>")); // Total count
        assertTrue(htmlContent.contains("<td>23501,50</td>")); // Total amount
    }

    @Test
    public void testGenerateReport_WithMissingPaymentMethods() throws IOException {
        Map<String, PaymentSummary> summary = new HashMap<>();

        PaymentSummary patSummary = new PaymentSummary();
        patSummary.documentCount = 2;
        patSummary.totalAmount = 5000.00;
        summary.put("PAT", patSummary);
        reportGenerator.generateReport(summary);
        Path reportPath = Paths.get("output", REPORT_FILE);
        String htmlContent = new String(Files.readAllBytes(reportPath));

        assertTrue(htmlContent.contains("<td>0</td>")); // PAC count
        assertTrue(htmlContent.contains("<td>0,00</td>")); // PAC amount
        assertTrue(htmlContent.contains("<td>0</td>")); // Empty count
        assertTrue(htmlContent.contains("<td>0,00</td>")); // Empty amount
    }


    @After
    public void tearDown() throws IOException {
        Path reportPath = Paths.get("output", REPORT_FILE);
        Files.deleteIfExists(reportPath);
        Files.deleteIfExists(Paths.get(TEST_OUTPUT_DIR));
    }

    private Map<String, PaymentSummary> createTestSummary() {
        Map<String, PaymentSummary> summary = new HashMap<>();

        PaymentSummary patSummary = new PaymentSummary();
        patSummary.documentCount = 5;
        patSummary.totalAmount = 12500.75;
        summary.put("PAT", patSummary);

        PaymentSummary pacSummary = new PaymentSummary();
        pacSummary.documentCount = 3;
        pacSummary.totalAmount = 7800.50;
        summary.put("PAC", pacSummary);

        PaymentSummary emptySummary = new PaymentSummary();
        emptySummary.documentCount = 2;
        emptySummary.totalAmount = 3200.25;
        summary.put("", emptySummary);

        return summary;
    }
}
