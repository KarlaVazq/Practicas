# Ejercicio: Patrón Proxy en Java

## 🎭 Contexto del Problema

Tenemos un servicio `Image` que carga una imagen desde disco, una operación costosa. Queremos postponer esta carga hasta que sea absolutamente necesario (carga perezosa - lazy loading). Además, queremos controlar el acceso al objeto real.

## 🎯 Tu Tarea

Transformar el código usando el **Patrón Proxy** para implementar carga perezosa (lazy loading) y controlar el acceso a los objetos costosos.

## 📋 Instrucciones Paso a Paso

### Paso 1: Crea la Clase Proxy

Crea una clase `ProxyImage` que implemente la interfaz `Image`:

```java
public class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;
    
    public ProxyImage(String filename) {
        this.filename = filename;
    }
}
```
### Paso 2: Implementa la Lógica de Carga Perezosa

En el método display() del ProxyImage, implementa la carga perezosa:
```java
@Override
public void display() {
    if (realImage == null) {
        realImage = new RealImage(filename); // Carga solo cuando es necesario
    }
    realImage.display(); // Delega al objeto real
}
```
### Paso 3: Modifica el Código Cliente

En la clase Main, cambia la creación de objetos para usar el Proxy en lugar del objeto real directamente:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Application started.");
        
        // Usando Proxy - la carga no ocurre inmediatamente
        Image image = new ProxyImage("photo.jpg");
        System.out.println("Proxy created, but RealImage not loaded yet.");
        
        // La carga solo ocurre aquí, cuando realmente necesitamos mostrar la imagen
        image.display();
        
        // Segunda llamada - ya está cargada, no se vuelve a cargar
        System.out.println("Second call:");
        image.display();
    }
}
```
### 🤔 Preguntas de Análisis

* Carga Perezosa: ¿Cuándo se ejecuta el constructor de RealImage en el código original vs en el código con Proxy?
* Transparencia: ¿El código cliente necesita saber si está trabajando con un Proxy o con el objeto real? ¿Por qué?
* Rendimiento: ¿Qué ventajas de rendimiento ofrece el patrón Proxy en este escenario?
* Control de Acceso: Además de la carga perezosa, ¿qué otros tipos de control podríamos implementar en el Proxy? (Ej: verificación de permisos, logging, cache)
* Principio SOLID: ¿Qué principio SOLID se aplica aquí al permitir que el Proxy y el RealImage implementen la misma interfaz?

### 🧪 Pruebas a Realizar

Ejecuta el código para verificar que:

Sin Proxy: El mensaje "Loading image..." aparece inmediatamente al crear el objeto
Con Proxy: El mensaje "Loading image..." aparece solo al llamar a display()
Múltiples llamadas: La carga solo ocurre una vez, incluso con múltiples llamadas a display()

### 💡 Escenarios de Extensión

Considera cómo extenderías este patrón para:

Proxy de Protección: Verificar permisos antes de cargar/mostrar la imagen
Proxy de Cache: Almacenar en cache imágenes ya cargadas
Proxy de Logging: Registrar cada vez que se accede a una imagen
Proxy Remoto: Acceder a imágenes en servidores remotos

---

## Resolución del ejercicio

**Estructura (carpeta `src/`):** `Image.java` (interfaz), `RealImage.java`, `ProxyImage.java`, `Main.java` y los proxies de extensión `ProxyImageProtegido.java`, `ProxyImageLog.java` y `ProxyImageCache.java`.

**Compilar y ejecutar**

```bash
mkdir -p bin
javac -d bin src/*.java
java -cp bin Main
```

**Pruebas realizadas**

| Prueba | Resultado |
|---|---|
| Sin proxy | `Loading image...` aparece apenas se crea el `RealImage`. |
| Con proxy | `Proxy created, but RealImage not loaded yet.` aparece primero; `Loading image...` solo al llamar `display()`. |
| Varias llamadas | La carga ocurre una sola vez; la segunda llamada solo muestra `Displaying`. |

**Preguntas de análisis**

1. **Carga perezosa.** En el código original, el constructor de `RealImage` (y su carga del disco) se ejecuta al crear el objeto. Con el proxy, se ejecuta en la primera llamada a `display()`.
2. **Transparencia.** No: el cliente usa la interfaz `Image` y no distingue si recibe el `ProxyImage` o el `RealImage`.
3. **Rendimiento.** Se evita cargar imágenes que nunca se muestran (menos tiempo de inicio y memoria) y se carga solo una vez aunque se muestre muchas veces.
4. **Otros controles.** Verificar permisos, registrar accesos (logging), guardar en caché, contar usos o acceder a un recurso remoto; los tres primeros están implementados como extensión.
5. **Principio SOLID.** Sustitución de Liskov, porque el proxy puede usarse donde se espere una `Image`; también Abierto/Cerrado e Inversión de dependencias, porque el cliente depende de la interfaz y no de `RealImage`.

**Extensiones implementadas**

- **Protección:** `ProxyImageProtegido` niega el acceso a roles no permitidos (`INVITADO`) antes de cargar nada.
- **Logging:** `ProxyImageLog` registra cada acceso (`[LOG] Acceso #n`).
- **Caché:** `ProxyImageCache` comparte la imagen ya cargada entre varios proxies: la carga ocurre una sola vez.
- **Remoto:** no se implementó; el proxy remoto tendría la misma interfaz `Image`, pero `display()` pediría la imagen a un servidor.
