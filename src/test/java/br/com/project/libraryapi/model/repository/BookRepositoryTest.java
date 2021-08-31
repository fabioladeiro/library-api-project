package br.com.project.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.project.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	BookRepository repository;

	@Test
	@DisplayName("Should return true when exist a saved book with the informed isbn")
	public void returnTrueWhenIsbnExists() {
		String isbn = "123";

		Book book = createNewBook();

		entityManager.persist(book);
		boolean existsByIsbn = repository.existsByIsbn(isbn);

		assertThat(existsByIsbn).isTrue();
	}

	@Test
	@DisplayName("Should return false when does nor exist a saved book with the informed isbn")
	public void returnTrueWhenIsbnDoesNotExists() {
		String isbn = "123";
		boolean existsByIsbn = repository.existsByIsbn(isbn);

		assertThat(existsByIsbn).isFalse();
	}

	@Test
	@DisplayName("Should get a book by id")
	public void findByIdTest() {
		Book book = createNewBook();
		entityManager.persist(book);
		Optional<Book> foundBook = repository.findById(book.getId());

		assertThat(foundBook.isPresent()).isTrue();
	}
	
	@Test
	@DisplayName("Should save a book")
	public void saveBookTest() {
		Book book = createNewBook();
		Book savedBook = repository.save(book);
		
		assertThat(savedBook.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Should delete a book")
	public void deleteBookTest() {
		Book book = createNewBook();
		entityManager.persist(book);
		
		Book foundBook = entityManager.find(Book.class, book.getId());

		repository.delete(foundBook);
		
		Book deletedBook = entityManager.find(Book.class, book.getId());
		assertThat(deletedBook).isNull();
	}

	private Book createNewBook() {
		return new Book("My book", "Author", "123");
	}
}
