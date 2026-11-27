public class Saludo {
    public static void main(String[] args) {
        String nombre = args.length > 0 ? args[0] : "mundo";
        System.out.println("¡Hola, " + nombre + "! Practicando Git dentro de un Dev Container.");
    }
}
