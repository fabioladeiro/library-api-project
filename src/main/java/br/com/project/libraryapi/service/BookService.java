package br.com.project.libraryapi.service;

import java.util.Optional;

import br.com.project.libraryapi.model.entity.Book;

public interface BookService {

	Book save(Book any);

	Optional<Book> getById(Long id);

	void delete(Long id);

	Book update(Book entity);

}
