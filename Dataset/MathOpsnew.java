public class MathOpsnew {

    public int square(int x) {
        int sq = x * x;  
        return sq;
    }

    public int cube(int x) {
        return x * x * x;
    }

    public int factorial(int n) {
        int result = 1;
        for(int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static void main(String[] args) {
        MathOps m = new MathOps();
        int s = m.square(5);           
        System.out.println("Square: " + s);
        System.out.println("Cube: " + m.cube(3));
    }
}
