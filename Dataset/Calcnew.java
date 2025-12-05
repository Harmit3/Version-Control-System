public class Calcnew {

    public int add(int a, int b) {
        int result = a + b;    
        return result;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public double divide(double a, double b) {
        if (b == 0) return 0;
        return a / b;
    }

    public static void main(String[] args) {
        Calcnew calc = new Calcnew();   
        int sum = calc.add(10, 20);           
        int product = calc.multiply(7, 8);    
        System.out.println("Sum: " + sum);    
        System.out.println("Product: " + product);
    }
}
