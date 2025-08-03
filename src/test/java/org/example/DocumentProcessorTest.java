package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DocumentProcessorTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private XMLGenerator xmlGenerator;

    @Mock
    private ReportGenerator reportGenerator;

    @InjectMocks
    private DocumentProcessor documentProcessor;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {

        MockitoAnnotations.initMocks(this);
        documentProcessor = new DocumentProcessor();

        // Inyecta mocks via reflection
        Field xmlGeneratorField = DocumentProcessor.class.getDeclaredField("xmlGenerator");
        xmlGeneratorField.setAccessible(true);
        xmlGeneratorField.set(documentProcessor, xmlGenerator);

        Field reportGeneratorField = DocumentProcessor.class.getDeclaredField("reportGenerator");
        reportGeneratorField.setAccessible(true);
        reportGeneratorField.set(documentProcessor, reportGenerator);

        Field objectMapperField = DocumentProcessor.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(documentProcessor, objectMapper);
    }

    @Test
    public void testProcessDocuments_Success() throws IOException {
        String testJson = createTestJson();
        String testDocumentJson = "{\"medioPago\":\"PAT\",\"totalAPagar\":100,50}";

        // Configurar mocks
        Document testDocument = new Document();
        testDocument.setMedioPago("PAT");
        testDocument.setTotalAPagar(100.50);

        JsonNode mockRootNode = mock(JsonNode.class);
        JsonNode mockDocumentsNode = mock(JsonNode.class);
        JsonNode mockDocNode = mock(JsonNode.class);
        JsonNode mockContentNode = mock(JsonNode.class);

        when(objectMapper.readTree(testJson)).thenReturn(mockRootNode);
        when(mockRootNode.get("documents")).thenReturn(mockDocumentsNode);
        when(mockDocumentsNode.iterator()).thenReturn(Arrays.asList(mockDocNode).iterator());
        when(mockDocNode.get("ContentBase64")).thenReturn(mockContentNode);
        when(mockContentNode.asText()).thenReturn(createTestGzipContent(testDocumentJson));
        when(objectMapper.readValue(testDocumentJson, Document.class)).thenReturn(testDocument);

        // 2. Ejecutar método
        documentProcessor.processDocuments(testJson);

        // 3. Verificar interacciones
        verify(xmlGenerator, times(1)).generateXML(testDocument);
        verify(reportGenerator, times(1)).generateReport(argThat(map ->
                map.containsKey("PAT") &&
                        map.get("PAT").documentCount == 1
        ));
    }



    @Test
    public void testDecompressBase64Gzip_WithValidContent() throws IOException {
        String originalContent = "test content";
        String compressed = createTestGzipContent(originalContent);

        String result = documentProcessor.decompressBase64Gzip(compressed);
        assertEquals(originalContent, result);
    }


    @Test
    public void testPaymentSummary_AddPayment() {
        DocumentProcessor.PaymentSummary summary = new DocumentProcessor.PaymentSummary();
        assertEquals(0, summary.documentCount);
        assertEquals(0.0, summary.totalAmount, 0.001);

        summary.addPayment(50.0);
        assertEquals(1, summary.documentCount);
        assertEquals(50.0, summary.totalAmount, 0.001);

        summary.addPayment(30.5);
        assertEquals(2, summary.documentCount);
        assertEquals(80.5, summary.totalAmount, 0.001);
    }

    private String createTestJson() {
        return "{\"documents\":[{\"ContentBase64\":\"" + createTestGzipContent("{}") + "\"}]}";
    }

    private String createTestGzipContent(String content) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            gos.write(content.getBytes());
            gos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
