public class Calc {

    public int add(int a, int b) {
        int tot;
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public static void main(String[] args) {
        Calc c = new Calc();
        int sum = c.add(3, 4);
        int prod = c.multiply(2, 5);
        System.out.println(sum);
        System.out.println(prod);
    }
}
