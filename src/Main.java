import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Introduce la ruta del archivo de log (ej. log_ejemplo.txt): ");
        String rutaLog = scanner.nextLine();

        System.out.print("Introduce la ruta del archivo de informe (ej. informe.txt): ");
        String rutaInforme = scanner.nextLine();

        AnalizadorLog analizador = new AnalizadorLog(rutaLog, rutaInforme);
        analizador.analizarLog();

        scanner.close();
    }
}
