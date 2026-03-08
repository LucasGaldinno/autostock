package br.com.AutoStock.exception;

public class CnpjInvalidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CnpjInvalidoException() {
        super();
    }

    public CnpjInvalidoException(String message) {
        super(message);
    }
}
