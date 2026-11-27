public class ControlRemotoAvanzado extends ControlRemoto {
    public ControlRemotoAvanzado(Dispositivo dispositivo) {
        super(dispositivo);
    }

    @Override
    public void togglePower() {
        System.out.println("Control Avanzado: Power inteligente");
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    @Override
    public void volumeDown() {
        System.out.println("Control Avanzado: Bajar volumen (-5)");
        dispositivo.setVolume(dispositivo.getVolume() - 5);
    }

    @Override
    public void volumeUp() {
        System.out.println("Control Avanzado: Subir volumen (+5)");
        dispositivo.setVolume(dispositivo.getVolume() + 5);
    }

    @Override
    public void channelDown() {
        System.out.println("Control Avanzado: Canal anterior");
        dispositivo.setChannel(dispositivo.getChannel() - 1);
    }

    @Override
    public void channelUp() {
        System.out.println("Control Avanzado: Canal siguiente");
        dispositivo.setChannel(dispositivo.getChannel() + 1);
    }

    // Funcionalidad exclusiva del control avanzado
    public void mute() {
        System.out.println("Control Avanzado: Silenciar");
        dispositivo.setVolume(0);
    }

    // Restricción cruzada: solo funciona si el dispositivo tiene la capacidad Grabable
    public void record() {
        System.out.println("Control Avanzado: Grabando programa actual");
        if (dispositivo instanceof Grabable) {
            ((Grabable) dispositivo).grabar();
        } else {
            System.out.println("  Este dispositivo no admite grabación");
        }
    }
}
