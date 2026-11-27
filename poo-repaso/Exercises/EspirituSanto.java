package Exercises;

public class EspirituSanto extends Hijo {
    String apellidoReal;

    public EspirituSanto(String nombre, String apellido, String apellidoReal) {
        super(nombre, apellido);
        this.apellidoReal = apellidoReal;
    }

    // Se compara con equals(), nunca con ==, porque son cadenas
    public Boolean pruebaParternidad() {
        return apellido.equals(apellidoReal);
    }
}
