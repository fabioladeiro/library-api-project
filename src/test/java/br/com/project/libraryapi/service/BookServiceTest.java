package br.com.project.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.project.libraryapi.exception.BusinessException;
import br.com.project.libraryapi.model.entity.Book;
import br.com.project.libraryapi.model.repository.BookRepository;
import br.com.project.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;

	@MockBean
	BookRepository repository;

	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}

	@Test
	@DisplayName("Should save a book")
	public void saveBookTest() throws BusinessException {
		Book book = createNewBook();
		Mockito.when(repository.save(book)).thenReturn(new Book(1l, "My book", "Author", "123456"));

		Book savedBook = service.save(book);
		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getIsbn()).isEqualTo("123456");
		assertThat(savedBook.getTitle()).isEqualTo("My book");
		assertThat(savedBook.getAuthor()).isEqualTo("Author");
	}

//	@Test
//	@DisplayName("Should generate a validation error when trying to register a book with isbn already used by another.")
//	public void shouldNotSaveABookWithDuplicatedISBN() {
//		Book book = createNewBook();
//		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
//		
//		Throwable catchThrowable = Assertions.catchThrowable(() -> service.save(book));
//		assertThat(catchThrowable)
//			.isInstanceOf(BusinessException.class)
//			.hasMessage("Isbn is already in use.");
//		
//		Mockito.verify(repository, Mockito.never()).save(book);
//	}

	@Test
	@DisplayName("Should get a book one book by id")
	public void getByIdTest() {
		Long id = 1l;
		Book book = createNewBook();
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

		Optional<Book> foundBook = service.getById(id);
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
	}

	@Test
	@DisplayName("Should return empty when a searched book by id does not exist")
	public void bookNotFoundByIdTest() {
		Long id = 1l;
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		Optional<Book> foundBook = service.getById(id);
		assertThat(foundBook.isPresent()).isFalse();
	}

	@Test
	@DisplayName("Should delete a book")
	public void deleteBookTest() {
		Book book = createNewBook();

		Assertions.assertDoesNotThrow(() -> service.delete(book.getId()));
		Mockito.verify(repository, Mockito.times(1)).deleteById(book.getId());

	}

	@Test
	@DisplayName("Should return an error when trying to delete a nonexistent book")
	public void deleteInvalidBookTest() {

		@SuppressWarnings("deprecation")
		Book book = new Book();
		Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book.getId()));
		Mockito.verify(repository, Mockito.never()).deleteById(book.getId());

	}

	@Test
	@DisplayName("Should update a book")
	public void updateBookTest() {

		Book updatingBook = createNewBook();
		updatingBook.setAuthor("New Author");

		Mockito.when(repository.save(updatingBook)).thenReturn(updatingBook);
		Book update = service.update(updatingBook);

		assertThat(update.getAuthor()).isEqualTo(updatingBook.getAuthor());
		assertThat(update.getId()).isEqualTo(updatingBook.getId());
		assertThat(update.getTitle()).isEqualTo(updatingBook.getTitle());
		assertThat(update.getIsbn()).isEqualTo(updatingBook.getIsbn());

	}

	@Test
	@DisplayName("Should return an error when trying to update a nonexistent book")
	public void updateInvalidBookTest() {

		@SuppressWarnings("deprecation")
		Book book = new Book();
		Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));
		Mockito.verify(repository, Mockito.never()).save(book);

	}

	@Test
	@DisplayName("Should filter books by properties")
	public void findBookTest() {

		Book book = createNewBook();
		List<Book> list = Arrays.asList(book);
		Page<Book> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
		
		Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
			.thenReturn(page);
		
		Page<Book> results = service.find(book, PageRequest.of(0, 10));

		assertThat(results.getTotalElements()).isEqualTo(1);
		assertThat(results.getContent()).isEqualTo(list);
		assertThat(results.getPageable().getPageNumber()).isEqualTo(0);
		assertThat(results.getPageable().getPageSize()).isEqualTo(10);
	}

	private Book createNewBook() {
		return new Book(1l, "My book", "Author", "123456");
	}
}
