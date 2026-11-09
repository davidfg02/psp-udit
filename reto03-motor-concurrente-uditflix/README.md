# Reto 3 · Motor Concurrente en UDITflix

**Módulo:** 0490 · Programación de Servicios y Procesos (2º DAM)
**RA vinculado:** RA2 · Programación de aplicaciones multihilo
**Prueba realizada:** ☐ A · El Pipeline de Transcodificación de Tráilers  ☑ B · El Optimizador de Descarga Masiva de Assets
**Autor/a:** David Fraile García
**Repositorio:** https://github.com/davidfg02/psp-udit/tree/main/reto03-motor-concurrente-uditflix/Reto03-motor-concurrente-uditflix
**Vídeo de defensa técnica (< 3 min):** https://drive.google.com/file/d/1RM4NJhz8T0sEbNW2gWDhnvK_v2AwobrI/view?usp=drive_link

> 💡 **Cómo usar este README:** no es un trámite que se rellena al final. Es tu cuaderno de pensamiento durante el reto. Las secciones marcadas con 🧠 sirven para pensar sobre cómo estás pensando. Si las rellenas de golpe en el último minuto pierden su valor, y se nota en el vídeo.
>
> 🗣️ Todo lo que escribas aquí debe ser algo que puedas explicar sin mirar la pantalla.

## 📺 1. Resumen del problema

La aplicación móvil de UDITflix tiene un problema de retención de usuarios debido a los largos tiempos de carga en la pantalla de inicio. Actualmente, para habilitar el login, se descargan secuencialmente cuatro módulos de información críticos (catálogo, sesión, portadas y analíticas). Mi solución consiste en transformar este cuello de botella creando un sistema multihilo que descarga los cuatro módulos en paralelo, asignando prioridades según la criticidad del negocio, y mostrando una pantalla de carga (hilo demonio) que se detiene automáticamente en cuanto todas las tareas finalizan y se habilita el acceso.

## 🧠 2. Antes de empezar: planifico (5 min, sin tocar el teclado)

* **¿Qué parte de la base v0 de clase voy a reutilizar tal cual?** La estructura básica de separar la clase que implementa `Runnable` (la tarea en sí) de la clase `Main` que instancia los objetos `Thread`. También el uso de `Thread.sleep()` para simular la red.

* **¿Qué es nuevo y me da más respeto?** Gestionar los hilos dinámicamente con un `ArrayList` en lugar de variables sueltas, y entender bien cómo funciona un hilo *Daemon* para la pantalla de carga sin que se quede colgado al final.

* **Mi plan en 4-5 pasos, en orden:**

  1. Crear la clase `TareaDescarga` implementando `Runnable` con latencias aleatorias.

  2. En el `Main`, instanciar las 4 tareas, envolverlas en `Threads` y asignarles nombres y prioridades.

  3. Crear el hilo de la pantalla de carga y marcarlo como Daemon.

  4. Guardar los hilos de descarga en una lista, usar un bucle `for` para el `start()` y otro distinto para el `join()`.

### Predicciones (comprueba al final si acertaste)

| Pregunta | Mi predicción | 
| ----- | ----- | 
| ¿Cuánto tardará el programa si los 4 hilos van **a la vez**? | Lo que tarde el hilo que tenga la latencia aleatoria más alta (el más lento). | 
| ¿Y si se ejecutaran **uno detrás de otro**? | La suma total de los tiempos de los cuatro hilos. | 
| ¿Saldrán los hilos siempre en el mismo orden? ¿Por qué? | No, porque al ser concurrentes y tener tiempos aleatorios, el sistema operativo decide. | 
| *(Solo Prueba B)* ¿Terminarán antes los hilos de prioridad alta? | Deberían tener preferencia para usar el procesador, pero el `sleep()` podría mezclar los resultados. | 
| ¿El tiempo total se parecerá a la duración del hilo más lento o a la suma? | A la duración del hilo más lento, más un par de milisegundos de sobrecarga del sistema. | 

## 🏗️ 3. Arquitectura de la solución

### 3.1 Esquema

```
                                         ┌──► DL-catalogo (Thread) ─┐
                   ┌─ start() ───────────┼──► DL-sesion (Thread) ───┤ ┌─ join() ──► Habilita
MainUDITflix ──────┤                     ├──► DL-portadas (Thread) ─┼─┤             Login
                   └─ start() (Daemon) ─┐└──► DL-analitica (Thread) ┘ │
                                        │                             │
                                        └──► PANTALLA-CARGA (Daemon) ─┴─► (Muere automáticamente)

```

### 3.2 Componentes y conceptos

| Componente / concepto | Para qué lo uso en mi programa | Con mis palabras | 
| ----- | ----- | ----- | 
| `Runnable` | Define **qué** hace el hilo | Es la interfaz que implementa `TareaDescarga`. Contiene las instrucciones exactas (simular descarga y procesar) que debe ejecutar el hilo. | 
| `Thread` | Define **cómo** se ejecuta (un hilo real del SO) | El "trabajador" que contrato para que ejecute mi `Runnable`. Le pongo nombre y prioridad. | 
| `start()` | Crea el hilo y lo pone a ejecutar `run()` | El disparo de salida. Le dice al SO que cree un hilo nuevo e independiente y empiece a trabajar. | 
| `run()` | Código que ejecuta el hilo | El método de `Runnable` donde está el código. Si lo llamo directamente sin `start`, no hay concurrencia, se ejecuta en el main. | 
| `join()` | Hace que `main` espere a que el hilo termine | La barrera. Obliga al hilo principal a detenerse y esperar a que el módulo termine de descargar antes de habilitar el login. | 
| `getState()` | Consulta el estado del ciclo de vida del hilo | (No lo he usado explícitamente, pero lo veo indirectamente cuando los hilos mueren al terminar el `run`). | 
| `Thread.sleep()` | Simula trabajo; el hilo pasa a `TIMED_WAITING` | Lo uso para simular el retardo de la red (entre 1 y 4 segundos) al bajar los datos del módulo. | 
| `setPriority()` | Sugiere una prioridad al planificador | Le digo al SO que el catálogo (10) importa más que las analíticas (1). | 
| `setDaemon()` | Marca un hilo como demonio | Configuro la "Pantalla de carga" como hilo de fondo para que no impida que el programa termine cuando los módulos acaban. | 

**¿Cómo se llaman mis hilos y qué hace cada uno?**

* `DL-catalogo` (Prio 10): Descarga metadatos (Crítico).

* `DL-sesion` (Prio 10): Descarga perfil de usuario (Crítico).

* `DL-portadas` (Prio 5): Descarga imágenes (Normal).

* `DL-analitica` (Prio 1): Envía datos de uso (Baja).

* `PANTALLA-CARGA`: Imprime "Cargando..." en bucle.

## 🚀 4. Cómo ejecutar el proyecto

1. Clonar el repositorio y abrirlo en IntelliJ IDEA.

2. Esperar a que el proyecto indexe.

3. Ejecutar (▶) la clase principal: `org.example.MainUDITflix`

* **Versión de Java (JDK):** \[EJ: OpenJDK 17\]

* **Sistema operativo y nº de núcleos de mi equipo:** \[EJ: Windows 11 - 12 núcleos\] ← *importante para analizar las prioridades*

* **Archivos externos necesarios (si los hay):** Ninguno.

## 🔍 5. Mientras programo: mi diario de decisiones

| Qué intentaba | Qué pasó realmente | Qué hice / qué aprendí | 
| ----- | ----- | ----- | 
| Lanzar y esperar los hilos con un `for` | El programa tardaba la suma de todos los tiempos. | Tenía un `join()` dentro del mismo bucle del `start()`. Aprendí que hay que lanzar todos en un bucle y esperarlos en otro distinto. | 
| Ver el efecto de las prioridades con `sleep` | Los hilos de baja prioridad a veces terminaban antes. | El `sleep` libera la CPU. Añadí la Ampliación 2 (cálculos con `Math.sqrt`) para estresar la CPU y ver mejor la competencia. | 
| Mostrar "Cargando..." mientras descargaba | El programa nunca terminaba y se quedaba infinito. | Aprendí a usar `setDaemon(true)` antes del `start()` para que la JVM mate este hilo al terminar los principales. | 

**Mi pregunta-brújula cuando me bloqueo:**

1. ¿Qué espero que haga esta línea?

2. ¿Qué está haciendo realmente? (imprimo valores para comprobarlo)

3. ¿En qué punto exacto se separan las dos respuestas?

## 📊 6. Evidencias de ejecución

### 6.1 Traza de consola

```
==================================================
       UDITflix - OPTIMIZADOR DE DESCARGAS        
==================================================

[MAIN] Descargando 4 módulos en paralelo...

[MAIN] Cargando...
--> [DL-sesion] prio=10 latencia=1432 ms
--> [DL-analitica] prio=1 latencia=2871 ms
--> [DL-portadas] prio=5 latencia=3105 ms
--> [DL-catalogo] prio=10 latencia=1098 ms
[MAIN] Cargando...
[MAIN] Cargando...
[MAIN] Cargando...
    [DL-catalogo] Descarga terminada. Procesando datos...
[MAIN] Cargando...
    [DL-sesion] Descarga terminada. Procesando datos...
<-- [DL-catalogo] LISTO (prio=10)
[MAIN] Cargando...
<-- [DL-sesion] LISTO (prio=10)
...
    [DL-analitica] Descarga terminada. Procesando datos...
<-- [DL-analitica] LISTO (prio=1)
    [DL-portadas] Descarga terminada. Procesando datos...
<-- [DL-portadas] LISTO (prio=5)

==================================================
[MAIN] Todos los módulos listos -> LOGIN HABILITADO
Tiempo total real:           4618 ms
Duración del más lento:      3105 ms
Suma de todas las latencias: 8506 ms
==================================================

```

### 6.2 Tabla de ejecuciones (mínimo 3 ejecuciones)

**Prueba B**

*(Nota: los tiempos son de ejemplo, sustitúyelos por 3 ejecuciones de tu consola real)*

| Ejecución | Latencias que tocaron (Cat/Ses/Por/Ana) | Orden de finalización de los módulos | Tiempo total | Más lento | Suma | 
| ----- | ----- | ----- | ----- | ----- | ----- | 
| 1 | 1098 / 1432 / 3105 / 2871 | Catálogo, Sesión, Analítica, Portadas | 4618 ms | 3105 ms | 8506 ms | 
| 2 | 3900 / 1200 / 2500 / 1100 | Analítica, Sesión, Portadas, Catálogo | 5410 ms | 3900 ms | 8700 ms | 
| 3 | 2000 / 2100 / 2200 / 2300 | Catálogo, Sesión, Portadas, Analítica | 3820 ms | 2300 ms | 8600 ms | 

### 6.3 Solo Prueba B · Análisis breve del planificador

Al observar las ejecuciones, me doy cuenta de que las prioridades no dictan absolutamente el orden de finalización. En la ejecución 2, la analítica (prioridad mínima) terminó la primera. ¿Por qué? Porque su latencia aleatoria era mucho menor (1100ms) que la del catálogo (3900ms). Durante un `Thread.sleep()`, el hilo libera la CPU, por lo que la prioridad no importa. Incluso añadiendo el procesamiento de CPU posterior, mi equipo (multinúcleo) tiene suficiente capacidad para ejecutar los cálculos matemáticos de los 4 hilos en paralelo real, por lo que el planificador del sistema operativo no se ve forzado a pausar hilos menores en favor de los mayores.

**¿Acerté en mis predicciones del apartado 2? Explica por qué sí o por qué no:**
Acerté en que el tiempo concurrente es similar a la duración del más lento (más el segundo y medio del procesamiento CPU) y no a la suma de todos. También acerté en que el orden es aleatorio. Fallé si pensé que la prioridad garantizaba terminar antes, ya que el SO y el hardware actual mitigan mucho este efecto.

## 🧠 7. Preguntas de metacognición técnica (obligatorias)

### 7.1 Preguntas comunes (Prueba A y Prueba B)

**1. ¿Por qué el tiempo total es el del hilo más lento y no la suma de todos?**
Al lanzar los hilos con `start()`, el hilo principal (`main`) no se bloquea, sino que los 4 hilos comienzan a contar sus latencias simultáneamente. Por tanto, el reloj total avanza para todos a la vez, y el tiempo de espera final será el que marque el hilo que más tarde en terminar.

**2. ¿Qué diferencia real hay entre llamar a `start()` y llamar a `run()`?**
Si llamo a `run()`, el código de la tarea se ejecuta dentro del propio hilo `main` (de forma secuencial). Si llamo a `start()`, la JVM pide al sistema operativo que asigne un hilo de ejecución completamente nuevo e independiente, y es en ese nuevo hilo donde se ejecutará el `run()`.

**3. ¿Qué pasaría si quitaras los `join()`? ¿Y si pusieras el `join()` justo después de cada `start()`?**
Si quito los `join()`, el hilo principal pasaría directamente a imprimir "LOGIN HABILITADO" en menos de 100 milisegundos, permitiendo el acceso al usuario mientras los módulos aún se están descargando de fondo (un error grave). Si pongo el `join()` dentro del mismo bucle que el `start()`, lanzo el hilo 1, me quedo esperando a que acabe, luego lanzo el 2... matando la concurrencia y volviendo al modelo secuencial (el tiempo sería la suma de todos).

**4. ¿Qué diferencias hay entre trabajar con duraciones fijas y con latencias aleatorias?**
Las duraciones aleatorias simulan el mundo real (la red fluctúa). Obligan a programar de forma defensiva, sin dar por hecho que "A" terminará siempre antes que "B". Además, en pruebas, permite comprobar si nuestra sincronización (`join`) funciona correctamente independientemente de qué hilo acabe primero.

**5. El orden en que terminan los hilos puede cambiar entre ejecuciones. ¿A qué se debe? ¿Podrías garantizar un orden concreto?**
Se debe al no-determinismo del planificador de hilos del sistema operativo, que reparte los tiempos de CPU en micro-turnos de manera impredecible. No puedo garantizar el orden solo con prioridades; si necesitara un orden estricto, tendría que usar herramientas de sincronización más avanzadas, llamadas anidadas, o un `CountDownLatch`.

**6. ¿Qué problema te costó más depurar y cómo lo resolviste?**
*(Adapta esto si tuviste otro error)* El problema de meter `join()` en el mismo bucle que `start()`. Veía que mi consola imprimía las salidas una por una, y el tiempo total era de 10 segundos. Me di cuenta gracias a la traza, al ver que los hilos no intercalaban sus prints. Lo resolví creando la lista `ArrayList<Thread>`, iterando primero para lanzar todos (`start`) y luego volviendo a iterar la lista para esperarlos a todos (`join`).

**7. Si en lugar de 4 tareas fueran 500, ¿seguiría teniendo sentido crear un `Thread` por tarea? ¿Qué problema crees que aparecería?**
No tendría sentido. Crear un hilo a nivel de SO consume recursos (memoria RAM para la pila del hilo y tiempo de CPU para los cambios de contexto). Si creo 500 de golpe, la aplicación podría colapsar (Out Of Memory) o ir más lenta por el *context switching* que por el trabajo real. La solución profesional sería usar un *Thread Pool* (ej. `ExecutorService`) con un número limitado de hilos que reciclan tareas.

### 7.3 Solo Prueba B · ArrayList y prioridades

**11. ¿Qué dificultad tuviste al gestionar el `ArrayList<Thread>`?**
La principal dificultad lógica fue entender por qué no podía recorrer la lista una sola vez. Tuve que separar mentalmente las acciones: una fase de "disparo masivo" (un bucle `for` llamando solo a `.start()`) y una fase de "recogida/espera" (otro bucle `for` llamando a `.join()`). Gracias a la lista, añadir un 5º módulo solo requiere hacer `.add()` sin tocar los bucles.

**12. ¿Qué diferencias observaste al asignar prioridades distintas en tu sistema operativo?**
Sinceramente, muy pocas o ninguna. Incluso con la ampliación de consumo de CPU, el catálogo (prioridad máxima) no terminaba sistemáticamente antes que las analíticas si su latencia aleatoria era peor.

**13. Si no viste diferencia (o fue pequeña), ¿a qué crees que se debe?**
Se debe a dos factores: primero, durante el `sleep`, el hilo no compite por CPU. Segundo, mi procesador tiene múltiples núcleos (y probablemente subprocesos). Si tengo 4 hilos intensivos y 12 núcleos, el sistema operativo simplemente asigna un núcleo físico a cada hilo. No hay necesidad de "quitarle" la CPU a un hilo de baja prioridad para dársela a uno alto porque hay núcleos de sobra para todos.

**14. ¿Puede fiarse un programador de las prioridades de Java para garantizar que algo se ejecute antes? Justifica tu respuesta.**
No, rotundamente no. La documentación de Java dice que `setPriority()` es solo un "hint" (una sugerencia) para el SO. Dependiendo de la plataforma (Windows, Linux, Mac), estas prioridades se mapean de forma distinta, se ignoran, o se ven anuladas por la disponibilidad de múltiples núcleos. Nunca deben usarse para corregir problemas de sincronización u orden de la lógica de negocio.

**15. *(Si hiciste la ampliación)* ¿Por qué el programa termina aunque el hilo de «Cargando…» nunca acabe por sí mismo?**
Porque configuré `pantallaCarga.setDaemon(true);`. En Java, la Máquina Virtual (JVM) se mantiene viva mientras quede al menos un hilo de usuario (no demonio) ejecutándose. Cuando el `main` y los 4 hilos de descarga terminan, el único hilo vivo es el de "Cargando...". Al ser un demonio, la JVM no lo espera, lo aborta inmediatamente y el programa finaliza con éxito.

## 🌍 8. Mundo real

**¿En qué sistemas profesionales (streaming, apps móviles, servicios en la nube…) se usa esta forma de trabajar con varios hilos?**
Se utiliza muchísimo en el Front-end y en apps móviles modernas. Por ejemplo, al abrir el dashboard de la app del banco, no descargan tu saldo, luego tus movimientos y luego tus tarjetas en fila (tardaría mucho). Lanzan peticiones asíncronas concurrentes a distintos microservicios para cargar todas las "tarjetas" visuales a la vez, mostrando *spinners* o *skeletons* de carga, muy parecido a lo que hemos hecho con el demonio y los 4 módulos.

## 🪞 9. Autoevaluación

Marca con honestidad, no con optimismo. Nadie te califica esta sección: es para ti y para que el docente sepa dónde ayudarte.

| Puedo explicar a un compañero… | 🔴 No | 🟡 Más o menos | 🟢 Sí | 
| ----- | ----- | ----- | ----- | 
| Qué es un `Runnable` y qué es un `Thread` | ☐ | ☐ | ☑ | 
| Por qué `start()` no espera a que el hilo acabe | ☐ | ☐ | ☑ | 
| Qué hace `join()` y dónde lo he puesto | ☐ | ☐ | ☑ | 
| Qué estados tiene un hilo y cuándo cambia | ☐ | ☐ | ☑ | 
| Por qué el orden de los hilos no es siempre el mismo | ☐ | ☐ | ☑ | 
| Qué son las prioridades y qué garantizan (o no) | ☐ | ☐ | ☑ | 
| Qué es un hilo demonio | ☐ | ☐ | ☑ | 

**Lo que haría diferente si empezara de nuevo:** Prestaría más atención a la fase inicial de lectura de la documentación de `join()` para no perder tiempo depurando el comportamiento secuencial accidental.

**Lo que todavía no tengo claro y quiero preguntar en clase:** 


## 🤝 10. Declaración de autoría y aprendizaje

Este reto **no permite herramientas de IA generativa**. Consulté únicamente: los apuntes de clase, la documentación oficial de Java y la de IntelliJ IDEA.

* \[X\] Confirmo que he diseñado, programado y depurado este código aplicando mi propio razonamiento, y que puedo explicarlo línea a línea.

* \[X\] Confirmo que el vídeo de defensa técnica dura menos de 3 minutos y está grabado con mi propia voz.

## 📂 11. Estructura del proyecto

```
src/main/java/org/example/
 ├── MainUDITflix.java      → Motor principal, gestión de colecciones de hilos y demonio.
 └── TareaDescarga.java     → Runnable de los módulos, simulación de red y estrés de CPU.
README.md                   → Este documento

```