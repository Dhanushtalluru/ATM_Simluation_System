package atm;

/*
  Thrown when a user enters an incorrect PIN.
 */
public class InvalidPinException extends Exception {
    public InvalidPinException(String message) {
        super(message);
    }
}
