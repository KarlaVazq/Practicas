import java.util.HashMap;
import java.util.Map;

/** Proxy de caché: varias instancias del proxy comparten la misma imagen ya cargada. */
public class ProxyImageCache implements Image {
    private static final Map<String, RealImage> CACHE = new HashMap<>();
    private final String filename;

    public ProxyImageCache(String filename) {
        this.filename = filename;
    }

    @Override
    public void display() {
        RealImage real = CACHE.get(filename);
        if (real == null) {
            real = new RealImage(filename);
            CACHE.put(filename, real);
        }
        real.display();
    }
}
