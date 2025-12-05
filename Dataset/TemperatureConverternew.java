import java.util.*;

public class TemperatureConverternew {

    public static void main(String[] args) {

        double[] celsiusTemps = {0, 20, 37, 100};      

        System.out.println("Converting Celsius to Fahrenheit:");       

        for(double c : celsiusTemps) {                                      
            double f = (c * 9.0 / 5.0) + 32;                                  
            System.out.println(c + " Celsius = " + f + " Fahrenheit");      
        }

        for(double c : celsiusTemps) {                                      
            double k = c + 273.15;                                           
            System.out.println(c + " Celsius = " + k + " Kelvin");          
        }

        System.out.println("All conversions completed successfully!");      
    }
}
