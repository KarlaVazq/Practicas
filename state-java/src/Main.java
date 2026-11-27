public class Main {
    public static void main(String[] args) {
        System.out.println("=== Flujo normal: pendiente -> procesado -> enviado -> entregado ===");
        Pedido p1 = new Pedido();
        p1.mostrarEstado();
        p1.procesar();
        p1.enviar();
        p1.entregar();
        p1.mostrarEstado();

        System.out.println("\n=== Cancelación de un pedido pendiente ===");
        Pedido p2 = new Pedido();
        p2.cancelar();
        p2.mostrarEstado();
        p2.procesar(); // no permitido

        System.out.println("\n=== Transiciones inválidas ===");
        Pedido p3 = new Pedido();
        p3.enviar();    // pendiente no se envía
        p3.entregar();  // pendiente no se entrega
        p3.procesar();
        p3.cancelar();  // procesado no se cancela
        p3.entregar();  // procesado no se entrega
        p3.mostrarEstado();
    }
}
