public class prg1new {

    public int add(int x, int y) {
        return x + y;
    }

    public int subtract(int x, int y) {
        return x - y;
    }

    public int multiply(int x, int y) {
        return x * y;
    }

    public int divide(int x, int y) {
        if (y == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero"); 
        }
        return x / y;
    }

    public int square(int x) { 
        return x * x;
    }
}
