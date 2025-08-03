package org.example;

import org.example.models.Document;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLGenerator {
    public void generateXML(Document documento) throws IOException {
        String xmlContent = String.format(
                "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n" +
                        "<DTE version=\"1.0\">\n" +
                        "  <Documento ID=\"%s\">\n" +
                        "    <Cliente>%s</Cliente>\n" +
                        "    <Tipo>%s</Tipo>\n" +
                        "    <TotalAPagar>%.2f</TotalAPagar>\n" +
                        "    <MedioPago>%s</MedioPago>\n" +
                        "  </Documento>\n" +
                        "</DTE>",
                documento.getId(),
                documento.getName(),
                documento.getType(),
                documento.getTotalAPagar(),
                documento.getMedioPago()
        );

        Files.createDirectories(Paths.get("output"));
        Files.write(Paths.get("output/" + documento.getId() + ".xml"), xmlContent.getBytes());
    }
}
