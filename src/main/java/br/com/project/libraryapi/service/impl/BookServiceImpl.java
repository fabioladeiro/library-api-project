package br.com.project.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.project.libraryapi.exception.BusinessException;
import br.com.project.libraryapi.model.entity.Book;
import br.com.project.libraryapi.model.repository.BookRepository;
import br.com.project.libraryapi.service.BookService;

@Service
public class BookServiceImpl implements BookService {

	private BookRepository repository;

	public BookServiceImpl(BookRepository repository) {
		this.repository = repository;
	}

	@Override
	public Book save(Book book) {
		try {
			if (repository.existsByIsbn(book.getIsbn())) {
				throw new BusinessException("Isbn is already in use.");
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}

		return repository.save(book);
	}

	@Override
	public Optional<Book> getById(Long id) {
		return repository.findById(id);
	}

	@Override
	public void delete(Long id) {
		repository.deleteById(id);
	}

	@Override
	public Book update(Book book) {
		return repository.save(book);
	}

}
