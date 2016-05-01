package exceptions;

/**
 * Public class to manage a custom exception for DAO
 * @author chromecrown
 */
public class DAOException extends Exception {

    public DAOException(String message) {
        super(message);
    }
}
