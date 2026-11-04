
package org.example;

import java.util.ArrayList;
import java.util.Random;

/**
 * UDITflix - Optimizador de descarga masiva.
 *
 * Cuatro hilos creados manualmente, con prioridades diferentes.
 * Se utiliza una lista dinámica para lanzarlos y sincronizarlos.
 *
 * Ampliación 1: pantalla de carga con hilo daemon.
 * Ampliación 2: procesamiento intensivo de CPU en cada tarea.
 */
public class MainUDITflix {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("==================================================");
        System.out.println("       UDITflix - OPTIMIZADOR DE DESCARGAS        ");
        System.out.println("==================================================");

        Random azar = new Random();

        // 1. Duración aleatoria entre 1000 y 4000 ms
        int d1 = 1000 + azar.nextInt(3001);
        int d2 = 1000 + azar.nextInt(3001);
        int d3 = 1000 + azar.nextInt(3001);
        int d4 = 1000 + azar.nextInt(3001);

        long inicio = System.currentTimeMillis();

        // 2. Creamos las tareas
        TareaDescarga tarea1 = new TareaDescarga("catalogo-metadatos", d1);
        TareaDescarga tarea2 = new TareaDescarga("sesion-y-perfil", d2);
        TareaDescarga tarea3 = new TareaDescarga("portadas-assets", d3);
        TareaDescarga tarea4 = new TareaDescarga("analiticas", d4);

        // 3. Creamos los hilos y asignamos sus prioridades
        Thread hilo1 = new Thread(tarea1, "DL-catalogo");
        Thread hilo2 = new Thread(tarea2, "DL-sesion");
        Thread hilo3 = new Thread(tarea3, "DL-portadas");
        Thread hilo4 = new Thread(tarea4, "DL-analitica");

        hilo1.setPriority(Thread.MAX_PRIORITY);
        hilo2.setPriority(Thread.MAX_PRIORITY);
        hilo3.setPriority(Thread.NORM_PRIORITY);
        hilo4.setPriority(Thread.MIN_PRIORITY);

        // 4. Guardamos todos los hilos en una lista dinámica
        ArrayList<Thread> hilos = new ArrayList<>();

        hilos.add(hilo1);
        hilos.add(hilo2);
        hilos.add(hilo3);
        hilos.add(hilo4);

        // 5. Ampliación opcional 1: pantalla de carga daemon
        Thread pantallaCarga = new Thread(() -> {

            while (true) {

                System.out.println("[MAIN] Cargando...");

                try {
                    Thread.sleep(300);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

        }, "PANTALLA-CARGA");

        // Configuramos el hilo como demonio
        pantallaCarga.setDaemon(true);

        // Lo lanzamos
        pantallaCarga.start();

        // 6. Lanzamos todos los hilos de descarga
        System.out.println("\n[MAIN] Descargando "
                + hilos.size() + " módulos en paralelo...\n");

        for (Thread hilo : hilos) {
            hilo.start();
        }

        // 7. Esperamos a que terminen todos los hilos
        for (Thread hilo : hilos) {
            hilo.join();
        }

        // 8. Calculamos los tiempos
        long total = System.currentTimeMillis() - inicio;

        int masLento = Math.max(
                Math.max(d1, d2),
                Math.max(d3, d4)
        );

        int suma = d1 + d2 + d3 + d4;

        // 9. Habilitamos el login
        System.out.println("\n==================================================");
        System.out.println("[MAIN] Todos los módulos listos -> LOGIN HABILITADO");
        System.out.println("Tiempo total real:           " + total + " ms");
        System.out.println("Duración del más lento:      " + masLento + " ms");
        System.out.println("Suma de todas las latencias: " + suma + " ms");
        System.out.println("==================================================");
    }
}
