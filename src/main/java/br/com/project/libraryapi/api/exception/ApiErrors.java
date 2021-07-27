package br.com.project.libraryapi.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

import br.com.project.libraryapi.exception.BusinessException;

public class ApiErrors {
	
	private List<String> errors;

	public ApiErrors(BindingResult bindingResult) {
		this.errors = new ArrayList<>();
		bindingResult.getAllErrors().forEach( error -> this.errors.add(error.getDefaultMessage()));
	}
	
	public ApiErrors(BusinessException exception) {
		this.errors = Arrays.asList(exception.getMessage());
	}

	public List<String> getErrors() {
		return errors;
	}
	
}
