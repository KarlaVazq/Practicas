public class Calculadora {
    static int sumar(int a, int b) { return a + b; }
    static int restar(int a, int b) { return a - b; }
    static int multiplicar(int a, int b) { return a * b; }

    public static void main(String[] args) {
        System.out.println("8 + 4 = " + sumar(8, 4));
        System.out.println("8 - 4 = " + restar(8, 4));
        System.out.println("8 x 4 = " + multiplicar(8, 4));
    }
}
