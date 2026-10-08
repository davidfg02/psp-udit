package org.example;

import java.io.IOException;
import java.io.InterruptedIOException;

public class PildoraParalelismo {
    public static void main(String[] args) {
        System.out.println("=====================");
        System.out.println("🚀 PILDORA TÉCNICA: SECUENCIAL VS PARALELISMO");
        System.out.println("=====================\n");

        try {
            System.out.println("INICIANDO EJECUCIÓN SECUENCIAL...");
            long inicioSecuencial = System.currentTimeMillis();
            System.out.println("    -> Lanzando proceso 1 (y esperando que muera...)");
            Process p1 = new ProcessBuilder("ping", "-n", "2", "127.0.0.1").start();
            p1.waitFor(); //CUIDADO: Java se congela aquí. El proceso 2 aún no existe
            //Una vez que el proceso 1 termina, lanzamos el segundo proceso
            System.out.println("    -> Lanzando proceso 2 (y esperando que muera...)");
            Process p2 = new ProcessBuilder("ping", "-n", "2", "8.8.8.8").start();
            p2.waitFor(); //Java se vuelve a congelar

            long finSecuencial = System.currentTimeMillis();
            System.out.println("⏱️ TIEMPO TOTAL SECUENCIAL: " + (finSecuencial - inicioSecuencial) + "ms\n");
            //2. EL CAMINO PARALELO(Ejecución solapada)
            System.out.println("=====================");
            System.out.println("INICIANDO EJECUCIÓN PARALELA...");
            //reseteo el cronometro
            long inicioParalelo = System.currentTimeMillis();

            //PASO 3. Apretamos todos los gatillos primero

            System.out.println("    ->Lanzando proceso 3 (!");
            Process p3 = new ProcessBuilder("ping", "-n", "2", "8.8.8.8").start();
            System.out.println("    ->Lanzando proceso 4 (!");
            Process p4 = new ProcessBuilder("ping", "-n", "2", "8.8.8.8").start();

            //PASO B. Ahora si le decimos a Java que recoja los resultados
            //Como ya están corriendo simultaneamente en el SO, el tiempo de espera se solapa
            System.out.println("    ->Bloqueando Java para recoger resultados");
            p3.waitFor();
            p4.waitFor();

            long finParalelo = System.currentTimeMillis();
            System.out.println("⏱️ TIEMPO TOTAL PARALELO: " + (finParalelo - inicioParalelo) + "ms\n");

        } catch (IOException e) {
            System.out.println("Error: No se pudo lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("Error: El proceso se interrumpió");
        }
    }
}
