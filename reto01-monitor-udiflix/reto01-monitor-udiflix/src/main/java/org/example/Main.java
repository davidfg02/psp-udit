package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        String[][] videos = {
                {"Animación 3D", "127.0.0.x"},
                {"Videojuegos", "127.0.0.x"},
                {"Kotlin", "127.0.0.x"},
                {"Android", "127.0.0.1"},
                {"Flutter", "127.0.0.1"},
        };

        System.out.println("==============================\nUDITFLIX - CATALOGO\n==============================");

        for (int i = 0; i < videos.length; i++) {
            String name = videos[i][0];
            String ip = videos[i][1];
            System.out.println("[VÍDEO] " + name);

            try {
                ProcessBuilder pb = new ProcessBuilder("ping", "-n", "1", ip);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                System.out.println("PID: " + process.pid());

                int codigo = process.waitFor();
                if (codigo == 0) {
                    System.out.println("Estado: ACTIVO");
                } else {
                    System.out.println("Estado: CAÍDO");
                }

            } catch (IOException e) {
                System.out.println("No se pudo lanzar el proceso");
            } catch (InterruptedException e) {
                System.out.println("La ejecución fue interrumpida");
            }
        }
        System.out.println("==============================\nCOMPROBACIÓN FINALIZADA\n==============================");

    }
}