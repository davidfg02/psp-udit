package org.example;

import java.io.IOException;

public class CodigoSalida {
    public static void main(String[] args) {
        System.out.println("===========================");
        System.out.println("COMPROBACIÓN DE SERVIDOR");
        System.out.println("===========================");
        try {
            //1. PREPARAMOS EL PROCESO EXTERNO
            //Vamos a ejecutar el comando "ping"
            //en Windows: ping -n 1 8.8.8.8
            //-n 1 -> realiza una sola comprobación
            //8.8.8.8 -> dirección IP
            ProcessBuilder pb = new ProcessBuilder("ping", "-n", "1", "8.8.8.8");

            //2. LANZAMOS EL PROCESO EXTERNO
            //start() ejecuta el proceso externo
            //El resultado de start() es un objeto Process
            Process process = pb.start();

            //3. OBTENEMOS EL PID
            //pid() nos permite conocer el identificador
            System.out.println("PID: " + process.pid());
            //4. ESPERAMOS A QUE TERMINE
            //waitfor() detiene nuestro programa en Java hasta que el proceso externo termine
            //Además, devuelve un número entero

            int codigoSalida = process.waitFor();

            //5. MOSTRAMOS EL CÓDIGO DE SALIDA
            System.out.println("Código de salida: " + codigoSalida);

            //6.INTERPRETAMOS EL RESULTADO
            //código 0 = resultado correcto
            //otro código = resultado no satisfactorio
            if (codigoSalida == 0) {
                System.out.println("Estado: ACTIVO");
            } else {
                System.out.println("Estado: CAÍDO");
            }
        } catch (IOException e) {
            System.out.println("Error al lanzar el proceso");
        } catch (InterruptedException e) {
            System.out.println("La espera del proceso fue interrumpida");
        }

        System.out.println("===========================");
        System.out.println("Fin de la comprobación");
        System.out.println("===========================");

    }
}