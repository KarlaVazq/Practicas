import java.util.Arrays;
import java.util.List;

import Exercises.*;

/**
 * Pruebas propias de las prácticas adicionales (Tests.java no se modifica).
 * Ejecución: java -cp out PruebasAdicionales
 */
public class PruebasAdicionales {
    static int fallos = 0;

    static void check(String nombre, boolean condicion) {
        System.out.println((condicion ? "OK    " : "FALLA ") + nombre);
        if (!condicion) fallos++;
    }

    static boolean lanza(Runnable r) {
        try {
            r.run();
            return false;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        // Ejercicios base
        DataTypes d = new DataTypes();
        check("DataTypes por defecto", d.cadena.equals("Hola Mundo") && d.entero == 10
                && d.flotante == 5.9f && d.caracter == 'A' && d.boleano);
        Colecciones c = new Colecciones();
        c.inicializar();
        java.util.HashMap<Integer, String> mapa = c.obtenerHash();
        check("Colecciones: 10 elementos únicos, claves 1..10",
                mapa.size() == 10 && mapa.containsKey(1) && mapa.containsKey(10));
        check("Padre.test() es false con otro apellido", !new Padre("Garcia").test());
        check("Alumno 5 -> :(", new Alumno("Ana", "Mate", 5).reprobar().equals(":("));
        check("Alumno 6 -> :)", new Alumno("Ana", "Mate", 6).reprobar().equals(":)"));
        check("Profesor 8", new Profesor("Luis", "Fisica", 8).reprobar().startsWith(">:)"));

        // Encapsulamiento
        CuentaBancaria cuenta = new CuentaBancaria("Sofía", 100);
        cuenta.depositar(50);
        cuenta.retirar(30);
        check("Cuenta: saldo 120", cuenta.getSaldo() == 120);
        check("Cuenta: depósito negativo", lanza(() -> cuenta.depositar(-1)));
        check("Cuenta: retiro > saldo", lanza(() -> cuenta.retirar(500)));

        // Abstracción
        Figura circulo = new Circulo(2);
        Figura rect = new Rectangulo(3, 4);
        check("Círculo área", Math.abs(circulo.calcularArea() - Math.PI * 4) < 1e-9);
        check("Rectángulo área", rect.calcularArea() == 12);
        check("Figura inválida", lanza(() -> new Circulo(0)) && lanza(() -> new Rectangulo(1, -1)));

        // Herencia
        check("Automóvil", new Automovil("Mazda", "2", 4).mostrarInformacion().equals("Mazda 2 - Puertas: 4"));
        check("Motocicleta", new Motocicleta("Honda", "Rebel", true)
                .mostrarInformacion().equals("Honda Rebel - Canastilla: true"));

        // Polimorfismo
        List<Notificacion> notificaciones = Arrays.asList(
                new NotificacionCorreo("alumno@ejemplo.com"), new NotificacionConsola());
        for (Notificacion n : notificaciones) {
            n.enviar("La actividad fue registrada");
        }
        check("Notificaciones ejecutadas", notificaciones.size() == 2);

        // Integración: biblioteca
        Biblioteca bib = new Biblioteca();
        Libro l1 = new Libro("Don Quijote"), l2 = new Libro("Pedro Páramo"), l3 = new Libro("Rayuela");
        bib.agregarLibro(l1); bib.agregarLibro(l2); bib.agregarLibro(l3);
        Usuario regular = new UsuarioRegular("Marta");
        bib.prestarLibro(l1, regular);
        bib.prestarLibro(l2, regular);
        check("Regular: límite de 2 préstamos", lanza(() -> bib.prestarLibro(l3, regular)));
        bib.devolverLibro(l1, regular);
        check("Devolución libera el libro", !l1.estaPrestado());
        Usuario premium = new UsuarioPremium("Diego");
        check("Premium permite 5", premium.getMaximoPrestamos() == 5);

        System.out.println(fallos == 0 ? "true" : "false (" + fallos + " fallos)");
        if (fallos > 0) System.exit(1);
    }
}
