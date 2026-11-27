/** Dispositivo nuevo: se agrega sin tocar ningún control remoto. */
public class Projector implements Dispositivo {
    private boolean on = false;
    private int volume = 20;
    private int channel = 1; // entrada HDMI 1, 2, 3...

    @Override
    public boolean isEnabled() { return on; }

    @Override
    public void enable() {
        on = true;
        System.out.println("Proyector encendido (lámpara calentando)");
    }

    @Override
    public void disable() {
        on = false;
        System.out.println("Proyector apagado (enfriando)");
    }

    @Override
    public int getVolume() { return volume; }

    @Override
    public void setVolume(int percent) {
        volume = Math.max(0, Math.min(100, percent));
    }

    @Override
    public int getChannel() { return channel; }

    @Override
    public void setChannel(int channel) {
        this.channel = Math.max(1, Math.min(3, channel)); // solo 3 entradas
    }

    @Override
    public void printStatus() {
        System.out.println("Proyector -> " + (on ? "encendido" : "apagado")
                + ", volumen " + volume + "%, entrada HDMI " + channel);
    }
}
