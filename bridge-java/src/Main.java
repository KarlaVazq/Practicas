public class Main {
    public static void main(String[] args) {
        System.out.println("=== Control Básico con Radio ===");
        ControlRemoto control1 = new ControlRemotoBasico(new Radio());
        probarControlRemoto(control1);

        System.out.println("\n=== Control Avanzado con TV ===");
        ControlRemotoAvanzado control2 = new ControlRemotoAvanzado(new TV());
        probarControlRemoto(control2);
        control2.mute();
        control2.record(); // la TV sí es Grabable

        System.out.println("\n=== Control Básico con TV ===");
        ControlRemoto control3 = new ControlRemotoBasico(new TV());
        probarControlRemoto(control3);

        System.out.println("\n=== Control Avanzado con Radio ===");
        ControlRemotoAvanzado control4 = new ControlRemotoAvanzado(new Radio());
        probarControlRemoto(control4);
        control4.record(); // la Radio no es Grabable: se maneja la restricción

        // ---- Ejercicios de extensión ----
        System.out.println("\n=== Extensión 1: Proyector con ambos controles ===");
        probarControlRemoto(new ControlRemotoBasico(new Projector()));
        probarControlRemoto(new ControlRemotoAvanzado(new Projector()));

        System.out.println("\n=== Extensión 2: Control de Voz ===");
        Dispositivo tv = new TV();
        ControlRemotoVoz voz = new ControlRemotoVoz(tv);
        voz.escuchar("enciende");
        voz.escuchar("canal 7");
        voz.escuchar("sube volumen");
        voz.escuchar("pon música");
        tv.printStatus();

        System.out.println("\n=== Flexibilidad: cambiar el dispositivo en tiempo de ejecución ===");
        voz.setDispositivo(new Radio());
        voz.escuchar("enciende");
        voz.escuchar("canal 3");
    }

    public static void probarControlRemoto(ControlRemoto control) {
        control.togglePower(); // Encender
        control.volumeUp();
        control.channelUp();
        control.volumeDown();
        control.dispositivo().printStatus();
        control.togglePower(); // Apagar
    }
}
