public class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        // Mp4Player no reproduce vlc
    }

    @Override
    public void playMp4(String fileName) {
        System.out.println("Reproduciendo archivo mp4: " + fileName);
    }
}
