package org.example;

import org.example.models.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;
public class XMLGeneratorTest {
    private static final String TEST_OUTPUT_DIR = "test_output";
    private XMLGenerator xmlGenerator;


    @Before
    public void setUp() {
        xmlGenerator = new XMLGenerator();
    }

    @Test
    public void testGenerateXML_CreatesValidFile() throws IOException {
        Document doc = new Document();
        doc.setId("TEST-001");
        doc.setName("Cliente Prueba");
        doc.setType("BOLETA");
        doc.setTotalAPagar(1500.75);
        doc.setMedioPago("TARJETA");

        xmlGenerator.generateXML(doc);

        Path expectedFile = Paths.get("output/TEST-001.xml");
        assertTrue("El archivo XML debe existir", Files.exists(expectedFile));

        String content = new String(Files.readAllBytes(expectedFile));
        assertTrue(content.contains("<Documento ID=\"TEST-001\">"));
        assertTrue(content.contains("<TotalAPagar>1500,75</TotalAPagar>"));

        Files.deleteIfExists(expectedFile);
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("output/TEST-001.xml"));
    }
}
