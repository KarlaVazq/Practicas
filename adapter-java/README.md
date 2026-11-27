# Patrón de Diseño Adapter - Ejercicios de Programación Java

## 🎯 Objetivo de Aprendizaje

Comprender cómo el patrón Adapter permite que interfaces incompatibles trabajen juntas creando un puente entre ellas.
---
## 🔧 Instrucciones de Transformación Paso a Paso

### 1. Identificar el Problema de Incompatibilidad

`AudioPlayer` solo puede reproducir archivos MP3.
Tenemos implementaciones de `AdvancedMediaPlayer` para VLC y MP4.
Las interfaces son incompatibles.

### 2. Crear la Clase `MediaAdapter`

Implementa la interfaz `MediaPlayer`.
Añade un campo para `AdvancedMediaPlayer`.
Crea un constructor que reciba `audioType` e inicialice el reproductor adecuado.

```java
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMusicPlayer;
    
    public MediaAdapter(String audioType) {
        if(audioType.equalsIgnoreCase("vlc")) {
            advancedMusicPlayer = new VlcPlayer();
        } else if(audioType.equalsIgnoreCase("mp4")) {
            advancedMusicPlayer = new Mp4Player();
        }
    }
}
```
### 3. Implementar el Método `play` en `MediaAdapter`

Delegar la llamada al método correspondiente de `AdvancedMediaPlayer`.
Manejar diferentes tipos de audio (vlc, mp4).

```java
@Override
public void play(String audioType, String fileName) {
    if(audioType.equalsIgnoreCase("vlc")) {
        advancedMusicPlayer.playVlc(fileName);
    } else if(audioType.equalsIgnoreCase("mp4")) {
        advancedMusicPlayer.playMp4(fileName);
    }
}
```

### 4. Modificar `AudioPlayer` para Usar el Adaptador

Añade una condición para usar `MediaAdapter` con formatos avanzados compatibles.
Mantén la funcionalidad actual de MP3.

```java
public class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;
    
    @Override
    public void play(String audioType, String fileName) {
        if(audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Reproduciendo archivo mp3: " + fileName);
        } else if(audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Tipo de medio inválido: " + audioType);
        }
    }
}
```
### 5. Probar la Solución Completa

Crea una clase de prueba para verificar que todos los tipos de medio funcionen correctamente.

```java
public class AdapterPatternDemo {
    public static void main(String[] args) {
        AudioPlayer audioPlayer = new AudioPlayer();
        
        audioPlayer.play("mp3", "song.mp3");
        audioPlayer.play("mp4", "video.mp4");
        audioPlayer.play("vlc", "movie.vlc");
        audioPlayer.play("avi", "movie.avi");
    }
}
```

---

## Resolución del ejercicio

**Estructura (carpeta `src/`):** `MediaPlayer.java` (interfaz objetivo), `AdvancedMediaPlayer.java` (interfaz incompatible), `VlcPlayer.java`, `Mp4Player.java`, `MediaAdapter.java`, `AudioPlayer.java` y `AdapterPatternDemo.java`.

**Roles del patrón**

| Rol | Clase |
|---|---|
| Interfaz objetivo | `MediaPlayer` |
| Adaptado | `AdvancedMediaPlayer` (`VlcPlayer`, `Mp4Player`) |
| Adaptador | `MediaAdapter` |
| Cliente | `AudioPlayer` |

**Cómo se resolvió**

1. El problema: `AudioPlayer` solo reproduce mp3 y `AdvancedMediaPlayer` tiene métodos distintos (`playVlc`, `playMp4`).
2. `MediaAdapter` implementa `MediaPlayer`, crea el reproductor adecuado según el formato y traduce `play(tipo, archivo)` al método correcto.
3. `AudioPlayer` conserva mp3 y, para `vlc` y `mp4`, delega en el adaptador; cualquier otro formato muestra `Tipo de medio inválido`.

**Compilar y ejecutar**

```bash
mkdir -p bin
javac -d bin src/*.java
java -cp bin AdapterPatternDemo
```

**Salida obtenida**

```text
Reproduciendo archivo mp3: song.mp3
Reproduciendo archivo mp4: video.mp4
Reproduciendo archivo vlc: movie.vlc
Tipo de medio inválido: avi
```

**Conclusión:** el adaptador deja que `AudioPlayer` use reproductores con interfaz incompatible sin modificarlos; agregar un formato nuevo solo implica un nuevo reproductor y una condición en el adaptador.
