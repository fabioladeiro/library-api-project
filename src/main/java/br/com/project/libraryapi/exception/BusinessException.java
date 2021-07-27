package br.com.project.libraryapi.exception;

public class BusinessException extends Throwable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
		super(message);
	}

}
