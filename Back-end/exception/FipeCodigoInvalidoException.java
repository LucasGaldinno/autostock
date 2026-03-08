package br.com.AutoStock.exception;

public class FipeCodigoInvalidoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FipeCodigoInvalidoException() {
        super();
    }

    public FipeCodigoInvalidoException(String message) {
        super(message);
    }
}
