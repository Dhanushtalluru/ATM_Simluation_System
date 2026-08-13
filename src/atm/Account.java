package atm;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/*
  Represents a single bank account holder's account.
  Encapsulates balance, PIN and transaction history.
  A Deque is used for transaction history so the most recent
  transactions can be pushed to the front and a mini statement
  (last N transactions) can be retrieved cheaply.
 */
public class Account {

    private final String accountNumber;
    private final String holderName;
    private String pin;
    private double balance;

    private final Deque<Transaction> transactionHistory = new LinkedList<>();
    private static final int MINI_STATEMENT_LIMIT = 5;

    public Account(String accountNumber, String holderName, String pin, double openingBalance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = openingBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    /*
      Verifies a PIN attempt against the stored PIN.
     */
    public boolean isPinCorrect(String pinAttempt) {
        return this.pin.equals(pinAttempt);
    }

    /*
      Changes the PIN after verifying the old one.
     */
    public void changePin(String oldPin, String newPin) throws InvalidPinException {
        if (!isPinCorrect(oldPin)) {
            throw new InvalidPinException("Old PIN is incorrect.");
        }
        this.pin = newPin;
        recordTransaction(TransactionType.PIN_CHANGE, 0.0);
    }

    /*
      Deposits a positive amount into the account.
     */
    public void deposit(double amount) {
        this.balance += amount;
        recordTransaction(TransactionType.DEPOSIT, amount);
    }

    /*
      Withdraws an amount if sufficient balance is available.
     */
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available balance: Rs. " + balance);
        }
        this.balance -= amount;
        recordTransaction(TransactionType.WITHDRAWAL, amount);
    }

    private void recordTransaction(TransactionType type, double amount) {
        transactionHistory.addFirst(new Transaction(type, amount, balance));
    }

    /*
      Returns an unmodifiable view of the last MINI_STATEMENT_LIMIT transactions.
     */
    public List<Transaction> getMiniStatement() {
        List<Transaction> recent = transactionHistory.stream()
                .limit(MINI_STATEMENT_LIMIT)
                .toList();
        return Collections.unmodifiableList(recent);
    }

    public List<Transaction> getFullStatement() {
        return Collections.unmodifiableList(new LinkedList<>(transactionHistory));
    }
}