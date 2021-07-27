package br.com.project.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
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

	private Book createNewBook() {
		return new Book(1l, "My book", "Author", "123456");
	}
}
