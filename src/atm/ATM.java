package atm;

import java.util.List;
import java.util.Scanner;

/*
  Console-based entry point for the ATM Simulation System.
  Handles login and drives the main transaction menu.
 */
public class ATM {

    private static final int MAX_PIN_ATTEMPTS = 3;
    private final Bank bank;
    private final Scanner scanner = new Scanner(System.in);

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public static void main(String[] args) {
        Bank bank = new Bank();
        seedSampleAccounts(bank);

        ATM atm = new ATM(bank);
        atm.start();
    }

    /** Pre-loads a couple of demo accounts so the app is usable immediately. */
    private static void seedSampleAccounts(Bank bank) {
        bank.addAccount(new Account("1001", "Ravi Kumar", "1234", 5000.0));
        bank.addAccount(new Account("1002", "Anita Sharma", "4321", 12000.0));
    }

    public void start() {
        System.out.println("========================================");
        System.out.println("   WELCOME TO THE ATM SIMULATION SYSTEM");
        System.out.println("========================================");

        boolean keepRunning = true;
        while (keepRunning) {
            Account account = login();
            if (account != null) {
                runSession(account);
            }
            keepRunning = askYesNo("\nDo you want another user to use the ATM? (y/n): ");
        }

        System.out.println("\nThank you for using the ATM. Goodbye!");
        scanner.close();
    }

    /**
     * Handles login: account number + PIN, with a limited number of attempts.
     */
    private Account login() {
        System.out.print("\nEnter Account Number: ");
        String accNo = scanner.nextLine().trim();

        if (!bank.accountExists(accNo)) {
            System.out.println("No account found with number: " + accNo);
            return null;
        }

        for (int attempt = 1; attempt <= MAX_PIN_ATTEMPTS; attempt++) {
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();
            try {
                Account account = bank.authenticate(accNo, pin);
                System.out.println("\nLogin successful. Welcome, " + account.getHolderName() + "!");
                return account;
            } catch (InvalidPinException e) {
                int remaining = MAX_PIN_ATTEMPTS - attempt;
                if (remaining > 0) {
                    System.out.println("Incorrect PIN. Attempts remaining: " + remaining);
                } else {
                    System.out.println("Too many incorrect attempts. Card blocked for this session.");
                }
            }
        }
        return null;
    }

    /**
     * Runs the main transaction menu loop for an authenticated account.
     */
    private void runSession(Account account) {
        boolean loggedIn = true;
        while (loggedIn) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleWithdraw(account);
                case "2" -> handleDeposit(account);
                case "3" -> handleBalanceEnquiry(account);
                case "4" -> handleMiniStatement(account);
                case "5" -> handlePinChange(account);
                case "6" -> {
                    loggedIn = false;
                    System.out.println("Logging out. Thank you, " + account.getHolderName() + "!");
                }
                default -> System.out.println("Invalid choice. Please select a valid option (1-6).");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n----------- ATM MENU -----------");
        System.out.println("1. Withdraw");
        System.out.println("2. Deposit");
        System.out.println("3. Balance Enquiry");
        System.out.println("4. Mini Statement");
        System.out.println("5. Change PIN");
        System.out.println("6. Exit / Logout");
        System.out.print("Choose an option: ");
    }

    private void handleWithdraw(Account account) {
        double amount = readPositiveAmount("Enter amount to withdraw: ");
        if (amount < 0) return;

        if (amount % 100 != 0) {
            System.out.println("Please enter an amount in multiples of 100.");
            return;
        }
        try {
            account.withdraw(amount);
            System.out.printf("Withdrawal successful. Please collect your cash.%n");
            System.out.printf("Available balance: Rs. %.2f%n", account.getBalance());
        } catch (InsufficientBalanceException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }

    private void handleDeposit(Account account) {
        double amount = readPositiveAmount("Enter amount to deposit: ");
        if (amount < 0) return;

        account.deposit(amount);
        System.out.printf("Deposit successful.%n");
        System.out.printf("Available balance: Rs. %.2f%n", account.getBalance());
    }

    private void handleBalanceEnquiry(Account account) {
        System.out.printf("Current balance: Rs. %.2f%n", account.getBalance());
    }

    private void handleMiniStatement(Account account) {
        List<Transaction> recent = account.getMiniStatement();
        System.out.println("\n------- MINI STATEMENT -------");
        System.out.println("Account: " + account.getAccountNumber() + " | " + account.getHolderName());
        if (recent.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            System.out.println("Date/Time            | Type         | Amount        | Balance");
            for (Transaction t : recent) {
                System.out.println(t);
            }
        }
    }

    private void handlePinChange(Account account) {
        System.out.print("Enter current PIN: ");
        String oldPin = scanner.nextLine().trim();
        System.out.print("Enter new PIN: ");
        String newPin = scanner.nextLine().trim();
        System.out.print("Confirm new PIN: ");
        String confirmPin = scanner.nextLine().trim();

        if (!newPin.equals(confirmPin)) {
            System.out.println("New PIN and confirmation do not match. PIN not changed.");
            return;
        }
        if (newPin.length() != 4 || !newPin.chars().allMatch(Character::isDigit)) {
            System.out.println("PIN must be exactly 4 digits.");
            return;
        }
        try {
            account.changePin(oldPin, newPin);
            System.out.println("PIN changed successfully.");
        } catch (InvalidPinException e) {
            System.out.println("PIN change failed: " + e.getMessage());
        }
    }

    /** Reads and validates a positive numeric amount from the console. */
    private double readPositiveAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return -1;
        }
    }

    private boolean askYesNo(String prompt) {
        System.out.print(prompt);
        String answer = scanner.nextLine().trim().toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }
}
