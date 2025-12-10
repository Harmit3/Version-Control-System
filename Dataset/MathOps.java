public class MathOps {

    public int square(int x) {
        System.out.println("square calc: ");
        return x * x;
    }

    public int cube(int x) {
        System.out.println("Cube calc: ");
        return x * x * x;
    }

    public static void main(String[] args) {
        MathOps m = new MathOps();
        System.out.println(m.square(4));
        System.out.println(m.cube(2));
        System.out.println(m.cube(3));
        return;
    }
}
