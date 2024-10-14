import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalizadorLog {
    private final String rutaLog;
    private final String rutaInforme;

    public AnalizadorLog(String rutaLog, String rutaInforme) {
        this.rutaLog = rutaLog;
        this.rutaInforme = rutaInforme;
    }

    public void analizarLog() {
        try {
            List<String> lineas = Files.readAllLines(Paths.get(rutaLog));

            Map<String, Long> conteoNiveles = lineas.stream()
                    .map(this::extraerNivel)
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            List<String> errores = lineas.stream()
                    .filter(linea -> linea.contains("ERROR"))
                    .map(this::extraerMensaje)
                    .collect(Collectors.toList());

            Map<String, Long> erroresComunes = errores.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            List<Map.Entry<String, Long>> topErrores = erroresComunes.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .collect(Collectors.toList());

            generarInforme(conteoNiveles, topErrores);

        } catch (IOException e) {
            System.err.println("Error al leer el archivo de log: " + e.getMessage());
        }
    }

    private String extraerNivel(String linea) {
        if (linea.contains("INFO")) return "INFO";
        if (linea.contains("WARNING")) return "WARNING";
        if (linea.contains("ERROR")) return "ERROR";
        return null;
    }

    private String extraerMensaje(String linea) {
        return linea.substring(linea.indexOf(" ") + 1);
    }

    private void generarInforme(Map<String, Long> conteoNiveles, List<Map.Entry<String, Long>> topErrores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaInforme))) {
            writer.write("Informe de Estadísticas del Log\n");
            writer.write("===============================\n");

            writer.write("\nConteo de Niveles de Log:\n");
            conteoNiveles.forEach((nivel, conteo) -> {
                try {
                    writer.write(String.format("%s: %d\n", nivel, conteo));
                } catch (IOException e) {
                    System.err.println("Error al escribir en el informe: " + e.getMessage());
                }
            });

            writer.write("\nTop 5 Mensajes de Error Más Comunes:\n");
            topErrores.forEach(entry -> {
                try {
                    writer.write(String.format("%s - %d ocurrencias\n", entry.getKey(), entry.getValue()));
                } catch (IOException e) {
                    System.err.println("Error al escribir en el informe: " + e.getMessage());
                }
            });

            System.out.println("Informe generado en: " + rutaInforme);

        } catch (IOException e) {
            System.err.println("Error al generar el informe: " + e.getMessage());
        }
    }
}

