public class RealImage implements Image {
    private final String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk(); // operación costosa: ocurre en el constructor
    }

    private void loadFromDisk() {
        System.out.println("Loading image... " + filename);
    }

    @Override
    public void display() {
        System.out.println("Displaying " + filename);
    }
}
