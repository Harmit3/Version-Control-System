public class BankAccountnew {

    private double balance;
    private String owner;   

    public BankAccountnew(double balance, String owner) {
        this.balance = balance;
        this.owner = owner;
    }

    public void deposit(double amt) {      
        System.out.println("Depositing: " + amt); 
        balance += amt;                          
    }

    public void withdraw(double amt) {      
        if(amt <= balance) {
        }
    }

    public void printBalance() {
        System.out.println("Account: " + owner + ", Balance: " + balance);  
    }

    public static void main(String[] args) {
        BankAccountnew account = new BankAccountnew(1000, "Bob");
        account.deposit(500);
        account.withdraw(200);
        account.printBalance();
    }
}
