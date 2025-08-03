package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

public class App
{
    public static void main( String[] args ) {
        Instant start = Instant.now();
        System.out.println("Proceso iniciado: " + start);
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get("lote.ejemplo.json")));
            DocumentProcessor processor = new DocumentProcessor();
            processor.processDocuments(jsonContent);
            Instant end = Instant.now();
            System.out.println("Proceso completado: " + end);
            System.out.println("Tiempo total de procesamiento: " +
                    Duration.between(start, end).toMillis() + " ms");
        } catch (IOException e) {
            System.err.println("Error al procesar el archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
