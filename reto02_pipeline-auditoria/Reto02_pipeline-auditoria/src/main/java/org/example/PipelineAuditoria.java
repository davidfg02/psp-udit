package org.example;

import java.io.IOException;

public class PipelineAuditoria {
    public static void main(String[] args) {
        System.out.println("==================");
        System.out.println("🚀PIPELINE AUDITORÍA");
        System.out.println("==================");

        try {
            System.out.println("=====================");
            System.out.println("INICIANDO EJECUCIÓN PARALELA...");

            System.out.println("    ->Lanzando proceso 1");
            Process p1 = new ProcessBuilder("ping", "-n", "2", "127.0.0.1").start();
            System.out.println("    ->Lanzando proceso 2");
            Process p2 = new ProcessBuilder("ping", "-n", "2", "error.invalid").start();

            int codigo1 = p1.waitFor();
            int codigo2 = p2.waitFor();
            System.out.println("Código proceso 1: " + codigo1);
            System.out.println("Código proceso 2: " + codigo2);

            if(codigo1 == 0 && codigo2 == 0){
                new ProcessBuilder("notepad.exe").start();
            } else{
                new ProcessBuilder("calc.exe").start();
            }
        } catch (IOException e) {
            System.out.println("Error: No se pudo lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("Error: El proceso se interrumpió");
        }
    }
}
