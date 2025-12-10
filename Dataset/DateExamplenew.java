
import java.text.*;
import java.util.*;
import java.util.Date;
 
public class DateExamplenew {
 
   public static void main(String args[]) {
 
     Date now = new Date();                                              
 
     DateFormat theDate = DateFormat.getDateInstance(DateFormat.LONG);   
     DateFormat germanDate = DateFormat.getDateInstance(
         DateFormat.LONG, Locale.GERMANY);                               
     DateFormat frenchDate = DateFormat.getDateInstance(DateFormat.LONG,
         Locale.FRANCE);                                                 
 
     System.out.println("Default locale: " + theDate.format(now));        
     System.out.println("German locale : " + germanDate.format(now));
     System.out.println("Formatted French date : " + frenchDate.format(now)); 
     
   } 
}
