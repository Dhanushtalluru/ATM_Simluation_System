package atm;
/* 
  Thrown when a withdrawal is attempted for more than the available balance.
 */

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
