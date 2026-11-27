# Ejercicio: Patrón Bridge en Java

## 🌉 Contexto del Problema

Tenemos diferentes tipos de dispositivos (e.g., `Radio`, `TV`) y diferentes tipos de controles remotos (e.g., `ControlRemotoBasico`, `ControlRemotoAvanzado`). Queremos evitar una explosión de clases como `ControlRemotoBasicoParaRadio`, `ControlRemotoAvanzadoParaTV`, etc., y permitir que ambas jerarquías evolucionen independientemente.

## 🎯 Tu Tarea

Analizar y comprender cómo el patrón Bridge desacopla la abstracción (controles remotos) de la implementación (dispositivos), permitiendo que ambas jerarquías varíen independientemente.

## 📋 Instrucciones Paso a Paso

### Paso 1: Identifica las Dos Jerarquías

**Jerarquía de Implementación (Dispositivos):**
- Interfaz `Dispositivo` con operaciones básicas de dispositivo
- Clases `Radio` y `TV` que implementan la interfaz

**Jerarquía de Abstracción (Controles):**
- Clase abstracta `ControlRemoto` que contiene referencia a un `Dispositivo`
- Clases `ControlRemotoBasico` y `ControlRemotoAvanzado` que extienden `ControlRemoto`

### Paso 2: Implementa la Interfaz Dispositivo

Crea la interfaz que define las operaciones que todos los dispositivos deben implementar:

```java
public interface Dispositivo {
    boolean isEnabled();
    void enable();
    void disable();
    int getVolume();
    void setVolume(int percent);
    int getChannel();
    void setChannel(int channel);
    void printStatus();
}
```
### Paso 3: Implementa las Clases Concretas de Dispositivos

Para Radio:

```java
public class Radio implements Dispositivo {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;
    
    @Override
    public boolean isEnabled() { return on; }
    
    @Override
    public void enable() { 
        on = true;
        System.out.println("Radio encendida");
    }
    
    @Override
    public void disable() { 
        on = false;
        System.out.println("Radio apagada");
    }
    
    // Implementa los demás métodos: getVolume, setVolume, getChannel, setChannel
    // y printStatus para mostrar el estado actual
}
```

Para TV:

```java
public class TV implements Dispositivo {
    private boolean on = false;
    private int volume = 30;
    private int channel = 1;
    
    @Override
    public boolean isEnabled() { return on; }
    
    @Override
    public void enable() { 
        on = true;
        System.out.println("TV encendida");
    }
    
    @Override
    public void disable() { 
        on = false;
        System.out.println("TV apagada");
    }
    
    // Implementa los demás métodos similares a Radio
}
```

### Paso 4: Crea la Abstracción Base (ControlRemoto)

Implementa la clase abstracta que contiene el "puente" hacia la implementación:

```java
public abstract class ControlRemoto {
    protected Dispositivo dispositivo; // ¡ESTE ES EL PUENTE!

    public ControlRemoto(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public abstract void togglePower();
    public abstract void volumeDown();
    public abstract void volumeUp();
    public abstract void channelDown();
    public abstract void channelUp();
}
```

### Paso 5: Implementa las Abstracciones Refinadas

Control Remoto Básico:

```java
public class ControlRemotoBasico extends ControlRemoto {
    public ControlRemotoBasico(Dispositivo dispositivo) {
        super(dispositivo);
    }

    @Override
    public void togglePower() {
        System.out.println("Control Básico: Botón power presionado");
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    @Override
    public void volumeDown() {
        System.out.println("Control Básico: Bajar volumen");
        dispositivo.setVolume(dispositivo.getVolume() - 10);
    }

    @Override
    public void volumeUp() {
        System.out.println("Control Básico: Subir volumen");
        dispositivo.setVolume(dispositivo.getVolume() + 10);
    }

    @Override
    public void channelDown() {
        System.out.println("Control Básico: Canal anterior");
        dispositivo.setChannel(dispositivo.getChannel() - 1);
    }

    @Override
    public void channelUp() {
        System.out.println("Control Básico: Canal siguiente");
        dispositivo.setChannel(dispositivo.getChannel() + 1);
    }
}
```

Control Remoto Avanzado:

```java
public class ControlRemotoAvanzado extends ControlRemoto {
    public ControlRemotoAvanzado(Dispositivo dispositivo) {
        super(dispositivo);
    }

    // Implementa los mismos métodos básicos
    @Override
    public void togglePower() {
        System.out.println("Control Avanzado: Power inteligente");
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    // ... implementa volumeDown, volumeUp, channelDown, channelUp

    // Método adicional solo disponible en el control avanzado
    public void mute() {
        System.out.println("Control Avanzado: Silenciar");
        dispositivo.setVolume(0);
    }

    public void record() {
        System.out.println("Control Avanzado: Grabando programa actual");
        // Lógica específica del control avanzado
    }
}
```

### Paso 6: Experimenta con Diferentes Combinaciones

En la clase Main, prueba diferentes combinaciones de controles y dispositivos:

```java
public class Main {
    public static void main(String[] args) {
        // Combinación 1: Control básico con Radio
        System.out.println("=== Control Básico con Radio ===");
        ControlRemoto control1 = new ControlRemotoBasico(new Radio());
        probarControlRemoto(control1);

        // Combinación 2: Control avanzado con TV
        System.out.println("\n=== Control Avanzado con TV ===");
        ControlRemotoAvanzado control2 = new ControlRemotoAvanzado(new TV());
        probarControlRemoto(control2);
        control2.mute(); // Funcionalidad exclusiva del control avanzado

        // Combinación 3: Control básico con TV
        System.out.println("\n=== Control Básico con TV ===");
        ControlRemoto control3 = new ControlRemotoBasico(new TV());
        probarControlRemoto(control3);

        // Combinación 4: Control avanzado con Radio
        System.out.println("\n=== Control Avanzado con Radio ===");
        ControlRemotoAvanzado control4 = new ControlRemotoAvanzado(new Radio());
        probarControlRemoto(control4);
        control4.record();
    }

    public static void probarControlRemoto(ControlRemoto control) {
        control.togglePower(); // Encender
        control.volumeUp();
        control.channelUp();
        control.volumeDown();
        control.togglePower(); // Apagar
    }
}
```

## 🔍 Puntos Clave del Bridge

El "Puente":

La composición protected Dispositivo dispositivo; en la clase ControlRemoto es el puente que conecta las dos jerarquías independientes.

Ventajas del Patrón:

Evita explosión de clases: Sin Bridge necesitaríamos:

ControlRemotoBasicoRadio
ControlRemotoBasicoTV
ControlRemotoAvanzadoRadio
ControlRemotoAvanzadoTV
Extensibilidad independiente: Podemos agregar nuevos dispositivos O nuevos controles sin afectar la otra jerarquía
### 🤔 Preguntas de Análisis

* Explosión de Clases: ¿Cuántas clases necesitaríamos si tuviéramos 3 tipos de controles y 4 tipos de dispositivos sin el patrón Bridge? ¿Y con el patrón Bridge?
* Acoplamiento: ¿Qué pasaría si la clase ControlRemotoBasico heredara directamente de Radio o TV en lugar de usar composición?
* Extensibilidad: Si quiero agregar un nuevo dispositivo (ej: Projector), ¿qué cambios necesito hacer? ¿Necesito modificar las clases de controles existentes?
* Principios SOLID: ¿Qué principios SOLID se aplican en este patrón? (Especialmente Principio de Inversión de Dependencias y * Principio Abierto/Cerrado)
* Flexibilidad: ¿Cómo nos beneficiamos al poder cambiar el dispositivo de un control en tiempo de ejecución?
### 🧪 Ejercicios de Extensión

* Agrega un nuevo dispositivo: Implementa la clase Projector y prueba con los controles existentes.
* Agrega un nuevo control: Crea ControlRemotoVoz que responda a comandos de voz.
* Funcionalidad cruzada: ¿Qué pasaría si algunos controles avanzados solo funcionaran con ciertos dispositivos? ¿Cómo manejarías esta restricción?

---

## Resolución del ejercicio

**Estructura (carpeta `src/`)**

| Jerarquía | Archivos |
|---|---|
| Implementación (dispositivos) | `Dispositivo.java`, `Radio.java`, `TV.java`, `Projector.java` (extensión) |
| Abstracción (controles) | `ControlRemoto.java`, `ControlRemotoBasico.java`, `ControlRemotoAvanzado.java`, `ControlRemotoVoz.java` (extensión) |
| Capacidad opcional | `Grabable.java` (solo las TV graban) |
| Prueba | `Main.java` con las 4 combinaciones del enunciado y las extensiones |

El puente es el campo `protected Dispositivo dispositivo` de `ControlRemoto`: los controles dependen de la interfaz, nunca de `Radio` o `TV`.

**Compilar y ejecutar**

```bash
mkdir -p bin
javac -d bin src/*.java
java -cp bin Main
```

**Preguntas de análisis**

1. **Explosión de clases.** Sin Bridge, 3 controles x 4 dispositivos = **12 clases**. Con Bridge: 3 + 4 = **7 clases** concretas (más la abstracción base y la interfaz). Cada nuevo tipo suma 1 clase, no una por combinación.
2. **Acoplamiento.** Si `ControlRemotoBasico` heredara de `Radio`, solo serviría para radios y quedaría atado a su código. Para usarlo con TV habría que duplicarlo, volviendo a la explosión de clases; además, un control no "es" una radio (la herencia sería incorrecta).
3. **Extensibilidad.** Para agregar `Projector` solo se crea esa clase que implementa `Dispositivo`. No se modifica ningún control existente (ver `Main`, extensión 1).
4. **SOLID.** *Abierto/Cerrado*: se agregan dispositivos y controles sin modificar lo existente. *Inversión de dependencias*: la abstracción depende de la interfaz `Dispositivo`, no de clases concretas. También *Responsabilidad única* (el control maneja botones y el dispositivo, su estado) y *Sustitución de Liskov* (cualquier `Dispositivo` o `ControlRemoto` se puede intercambiar).
5. **Flexibilidad.** Con `setDispositivo(...)` el mismo control cambia de dispositivo mientras el programa corre (por ejemplo, un control de voz que pasa de la TV a la radio) sin crear otro objeto.

**Ejercicios de extensión**

- **`Projector`:** nuevo dispositivo con 3 entradas HDMI como "canales"; funciona con los controles Básico y Avanzado sin cambios.
- **`ControlRemotoVoz`:** interpreta comandos de texto como `"enciende"`, `"sube volumen"` o `"canal 7"` y reutiliza cualquier dispositivo.
- **Funcionalidad cruzada:** se modela con una capacidad opcional. `Grabable` la implementan solo los dispositivos que graban (`TV`); `ControlRemotoAvanzado.record()` comprueba `dispositivo instanceof Grabable` y, si no la tiene, avisa que no se admite (así `Radio` no rompe el programa). Otra opción es lanzar una excepción o validar al construir el control.

