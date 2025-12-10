
import java.util.*;

public class TemperatureConverter {

    public static void main(String[] args) {

        // Temperatures in Celsius
        double[] celsiusTemps = {0, 20, 37, 100};

        System.out.println("Celsius to Fahrenheit conversion:");

        for(double c : celsiusTemps) {
            double f = (c * 9/5) + 32;
            System.out.println(c + " °C = " + f + " °F");
        }
        
        System.out.println("Conversion complete!");
        return;
    }
}
