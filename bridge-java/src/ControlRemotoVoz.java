/** Nuevo control: interpreta comandos de voz (texto) y reutiliza cualquier Dispositivo. */
public class ControlRemotoVoz extends ControlRemoto {
    public ControlRemotoVoz(Dispositivo dispositivo) {
        super(dispositivo);
    }

    public void escuchar(String comando) {
        String c = comando.toLowerCase().trim();
        System.out.println("Control de Voz escucha: \"" + comando + "\"");
        if (c.equals("enciende") || c.equals("apaga")) {
            togglePower();
        } else if (c.equals("sube volumen")) {
            volumeUp();
        } else if (c.equals("baja volumen")) {
            volumeDown();
        } else if (c.startsWith("canal ")) {
            try {
                dispositivo.setChannel(Integer.parseInt(c.substring(6).trim()));
                System.out.println("Control de Voz: canal fijado");
            } catch (NumberFormatException e) {
                System.out.println("Control de Voz: número de canal no válido");
            }
        } else {
            System.out.println("Control de Voz: no entendí el comando");
        }
    }

    @Override
    public void togglePower() {
        System.out.println("Control de Voz: alternando energía");
        if (dispositivo.isEnabled()) {
            dispositivo.disable();
        } else {
            dispositivo.enable();
        }
    }

    @Override
    public void volumeDown() {
        dispositivo.setVolume(dispositivo.getVolume() - 10);
    }

    @Override
    public void volumeUp() {
        dispositivo.setVolume(dispositivo.getVolume() + 10);
    }

    @Override
    public void channelDown() {
        dispositivo.setChannel(dispositivo.getChannel() - 1);
    }

    @Override
    public void channelUp() {
        dispositivo.setChannel(dispositivo.getChannel() + 1);
    }
}
