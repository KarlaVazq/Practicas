public class Main {
    public static void main(String[] args) {
        System.out.println("=== Sin proxy: la carga ocurre al crear el objeto ===");
        System.out.println("Application started.");
        Image directa = new RealImage("foto-sin-proxy.jpg");
        System.out.println("RealImage creado (ya se cargó).");
        directa.display();

        System.out.println("\n=== Con proxy: la carga ocurre al llamar display() ===");
        Image image = new ProxyImage("photo.jpg");
        System.out.println("Proxy created, but RealImage not loaded yet.");
        image.display();
        System.out.println("Second call:");
        image.display();

        System.out.println("\n=== Extensión: proxy de protección ===");
        new ProxyImageProtegido("privada.png", "INVITADO").display();
        new ProxyImageProtegido("privada.png", "ADMIN").display();

        System.out.println("\n=== Extensión: proxy de logging ===");
        Image conLog = new ProxyImageLog("paisaje.jpg");
        conLog.display();
        conLog.display();

        System.out.println("\n=== Extensión: proxy de caché (dos proxies, una sola carga) ===");
        new ProxyImageCache("logo.png").display();
        new ProxyImageCache("logo.png").display();
    }
}
