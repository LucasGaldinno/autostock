package br.com.AutoStock.exception;

public class CnpjDuplicadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CnpjDuplicadoException() {
        super();
    }

    public CnpjDuplicadoException(String message) {
        super(message);
    }
}
