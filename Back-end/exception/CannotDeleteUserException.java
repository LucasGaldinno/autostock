package br.com.AutoStock.exception;

public class CannotDeleteUserException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CannotDeleteUserException(String message) {
        super(message);
    }

    public CannotDeleteUserException(String message, Throwable cause) {
        super(message, cause);
    }
}
