
package org.example;

/**
 * UDITflix - Simulador de descarga de módulos.
 *
 * Implementa Runnable para definir el trabajo
 * que realizará cada hilo.
 *
 * Ampliación opcional: procesamiento intensivo de CPU.
 */
public class TareaDescarga implements Runnable {

    private final String nombreModulo;
    private final int latenciaMs;

    public TareaDescarga(String nombreModulo, int latenciaMs) {
        this.nombreModulo = nombreModulo;
        this.latenciaMs = latenciaMs;
    }

    @Override
    public void run() {

        String hiloActual = Thread.currentThread().getName();
        int prioridad = Thread.currentThread().getPriority();

        // 1. Comienza la descarga
        System.out.println("--> [" + hiloActual + "] prio="
                + prioridad + " latencia=" + latenciaMs + " ms");

        try {
            // Simulamos la descarga
            Thread.sleep(latenciaMs);

        } catch (InterruptedException e) {

            System.err.println("!--> [" + hiloActual
                    + "] DESCARGA CANCELADA.");

            Thread.currentThread().interrupt();
            return;
        }

        // 2. Termina la descarga y comienza el procesamiento
        System.out.println("    [" + hiloActual
                + "] Descarga terminada. Procesando datos...");

        // 3. Ampliación opcional 2: consumo de CPU durante 1,5 segundos
        long inicioProcesado = System.nanoTime();
        long duracionProcesado = 1_500_000_000L;

        double resultado = 0;
        long iteraciones = 0;

        while (System.nanoTime() - inicioProcesado < duracionProcesado) {

            resultado += Math.sqrt((iteraciones % 1000) + 1);
            iteraciones++;

            // Comprobamos si el hilo ha sido interrumpido
            if (Thread.currentThread().isInterrupted()) {
                System.err.println("!--> [" + hiloActual
                        + "] PROCESAMIENTO CANCELADO.");
                return;
            }
        }

        // 4. Finalización
        System.out.println("<-- [" + hiloActual
                + "] LISTO (prio=" + prioridad + ")");

    }

    public String getNombreModulo() {
        return nombreModulo;
    }

    public int getLatenciaMs() {
        return latenciaMs;
    }
}
