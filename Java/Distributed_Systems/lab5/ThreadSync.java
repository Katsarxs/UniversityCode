package lab5;

/*
 * Example of race condition.
 * If we delete synchronized, the account balance might be -50.
 */

public class ThreadSync {
    public static void main(String[] args) {
        Account account = new Account(150);
        AccountOwner Bob = new AccountOwner(account);
        AccountOwner Alice = new AccountOwner(account);
        Bob.start();
        Alice.start();
    }
}

class Account {
    private double balance;

    Account(double balance) {
        this.balance = balance;
    }

    synchronized void deposit(double amount) {
        balance += amount;
    }

    synchronized void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        }
    }
}

class AccountOwner extends Thread {
    private Account account;

    AccountOwner(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        account.withdraw(100);
    }

}