/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author lucas
 */
public class DatabaseOperationFailedException extends Exception {
    public DatabaseOperationFailedException() {
        super("Connection to database has failed. Please verify your internet connection. If you have internet access, please contact Lucas for more information.");
    }
    public DatabaseOperationFailedException(String message) {
        super(message);
    }
    public DatabaseOperationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    public DatabaseOperationFailedException(Throwable cause) {
        super(cause);
    }
}
