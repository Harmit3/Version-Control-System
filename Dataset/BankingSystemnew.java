import java.util.*;

public class BankingSystemnew {

    public void processAccounts(List<Double> balances) {  
        double totalBankBalance = 0;
        double interest = 0; 

        for(double balance : balances) {                      
            System.out.println("Processing balance: " + balance); 
            totalBankBalance += balance;
            if(balance > 3000) {                               
                System.out.println("High balance account");
            }
        }

        if(totalBankBalance > 10000) {                         
            System.out.println("High total bank balance!");
        }

        interest = totalBankBalance * 0.02;                   
        totalBankBalance += interest;                           
        System.out.println("Total bank balance with interest: " + totalBankBalance);
        System.out.println("All accounts processed!");          
    }

    public void notifyBankers() {                             
        System.out.println("Bankers notified about high balances.");
    }

    public static void main(String[] args) {
        List<Double> balances = Arrays.asList(2000.0, 5000.0, 4000.0);
        BankingSystemnew bankSystem = new BankingSystemnew();         
        bankSystem.processAccounts(balances);
        bankSystem.notifyBankers();                              
    }
}
