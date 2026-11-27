/** Proxy de protección: solo ciertos roles pueden ver la imagen. */
public class ProxyImageProtegido implements Image {
    private final Image imagen;
    private final String rol;

    public ProxyImageProtegido(String filename, String rol) {
        this.imagen = new ProxyImage(filename); // además conserva la carga perezosa
        this.rol = rol;
    }

    @Override
    public void display() {
        if (!"ADMIN".equals(rol) && !"USER".equals(rol)) {
            System.out.println("Acceso denegado para el rol: " + rol);
            return;
        }
        imagen.display();
    }
}
