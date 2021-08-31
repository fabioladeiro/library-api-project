package br.com.project.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
		if(id == null) {
			throw new IllegalArgumentException("Book id can not be null");
		}
		repository.deleteById(id);
	}

	@Override
	public Book update(Book book) {
		if(book	== null || book.getId() == null) {
			throw new IllegalArgumentException("Book id can not be null");
		}
		return repository.save(book);
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		Example<Book> example = Example.of(filter,
				ExampleMatcher
					.matching()
					.withIgnoreCase()
					.withIgnoreNullValues()
					.withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example, pageRequest);
	}

}
