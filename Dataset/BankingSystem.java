import java.util.*;

public class BankingSystem {

    public void processAccounts(List<Double> balances) {
        double totalBankBalance = 0;
        System.out.println("Processing accounts...");
        for(double balance : balances) {
            System.out.println("Account balance: " + balance);
            totalBankBalance += balance;
        }
        if(totalBankBalance > 10000) {
            System.out.println("High total balance detected");
        }
        System.out.println("Total balance: " + totalBankBalance);
        System.out.println("Processing complete!");
    }

    public static void main(String[] args) {
        List<Double> balances = Arrays.asList(2000.0, 5000.0, 4000.0);
        BankingSystem bank = new BankingSystem();
        bank.processAccounts(balances);
    }
}
