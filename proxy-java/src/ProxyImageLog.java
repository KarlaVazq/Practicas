/** Proxy de logging: registra cada acceso a la imagen. */
public class ProxyImageLog implements Image {
    private final Image imagen;
    private final String filename;
    private int accesos = 0;

    public ProxyImageLog(String filename) {
        this.filename = filename;
        this.imagen = new ProxyImage(filename);
    }

    @Override
    public void display() {
        accesos++;
        System.out.println("[LOG] Acceso #" + accesos + " a " + filename);
        imagen.display();
    }
}
