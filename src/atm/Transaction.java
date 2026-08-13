package atm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
  Immutable record of a single transaction performed on an account.
  Every action (deposit, withdrawal, pin change, balance check)
  creates one Transaction which is stored in the account's history.
 */
public class Transaction {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;

    public Transaction(TransactionType type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%-20s | %-12s | ₹%-12.2f | Bal: ₹%-12.2f",
                timestamp.format(FORMATTER), type, amount, balanceAfter);
    }
}
