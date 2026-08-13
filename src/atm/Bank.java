package atm;

import java.util.HashMap;
import java.util.Map;

/*
  Represents the bank: a repository/collection of all accounts.
  Uses a HashMap for O(1) account lookup by account number.
 */
public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    /**
     * Authenticates a user by account number and PIN.
     *
     * @return the Account if credentials are valid
     * @throws InvalidPinException if account does not exist or PIN is wrong
     */
    public Account authenticate(String accountNumber, String pin) throws InvalidPinException {
        Account account = accounts.get(accountNumber);
        if (account == null || !account.isPinCorrect(pin)) {
            throw new InvalidPinException("Invalid account number or PIN.");
        }
        return account;
    }
}
