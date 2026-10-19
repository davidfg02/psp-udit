package org.example;

public class TareaPrueba implements Runnable {

    //El nombre que identifica la tarea, ejemplo: hiloA
    private String nombre;

    public TareaPrueba(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void run() {
        //Repetimos el bloque 3 veces
        for (int i = 1; i <= 3; i++) {
            //Mostrar el nombre de la tarea 3 veces
            //El hilo que ejecuta esta instruccion y el paso
            //Con start() podremos ver el hilo A o el hilo B
            //con run() llamado desde main, veremos "main" en secuencia "cascada"

            System.out.println(nombre + " [" + Thread.currentThread().getName() + "] - Paso " + i);

            try {
                //Detiene temporalmente el hilo actual durante 100ms
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
