package br.com.project.libraryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.project.libraryapi.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

	boolean existsByIsbn(String isbn);

}
