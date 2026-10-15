
package org.example;

import java.io.*;
import java.util.ArrayList;

public class PipelineAuditoria {

    public static void main(String[] args) {

        long inicio = System.currentTimeMillis();

        System.out.println("==================");
        System.out.println("🚀PIPELINE AUDITORÍA");
        System.out.println("==================");

        ArrayList<String> servidores = new ArrayList<>();
        // Leer las direcciones desde el archivo
        try (BufferedReader br = new BufferedReader(new FileReader("servidores.txt"))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();

                if (!linea.isEmpty()) {
                    servidores.add(linea);
                    System.out.println("Servidor cargado: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: No se pudo leer el archivo.");
            e.printStackTrace();
            return;
        }


        ArrayList<Process> procesos = new ArrayList<>();

        int contadorExitos = 0;
        int contadorFallos = 0;

        try {
            System.out.println("=====================");
            System.out.println("INICIANDO EJECUCIÓN PARALELA...");

            // Bucle 1: lanzar todos los procesos
            for (String ip : servidores) {
                System.out.println("    -> Lanzando proceso a " + ip);

                Process proceso = new ProcessBuilder(
                        "ping", "-n", "1", "-w", "3000", ip
                ).start();

                procesos.add(proceso);
            }

            // Bucle 2: recoger los resultados
            for (Process proceso : procesos) {
                int codigo = proceso.waitFor();

                if (codigo == 0) {
                    contadorExitos++;
                } else {
                    contadorFallos++;
                }
            }

            System.out.println("=====================");
            System.out.println("Éxitos: " + contadorExitos);
            System.out.println("Fallos: " + contadorFallos);

            // Decisión final
            if (contadorFallos == 0) {
                new ProcessBuilder("notepad.exe").start();

            } else if (contadorExitos > 0 && contadorFallos > 0) {
                new ProcessBuilder("calc.exe").start();

            } else {
                new ProcessBuilder("explorer.exe").start();
            }

        } catch (IOException e) {
            System.out.println("Error: No se pudo lanzar el proceso.");

        } catch (InterruptedException e) {
            System.out.println("Error: El proceso se interrumpió.");
            Thread.currentThread().interrupt();

        }
        System.out.println(
                "Tiempo total: "
                        + (System.currentTimeMillis() - inicio)
                        + " ms"
        );
    }
}
