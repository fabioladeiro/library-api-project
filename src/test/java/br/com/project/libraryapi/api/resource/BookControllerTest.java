package br.com.project.libraryapi.api.resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.project.libraryapi.api.dto.BookDto;
import br.com.project.libraryapi.model.entity.Book;
import br.com.project.libraryapi.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books";

	@Autowired
	MockMvc mvc;

	@MockBean
	BookService service;

	@Test
	@DisplayName("Should create a book successfully.")
	public void createBookTest() throws Exception {

		BookDto dto = createNewBookDto();
		Book savedBook = new Book(1l, "My book", "Author", "123456");

		BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);

		mvc.perform(request).andExpect(status().isCreated()).andExpect(jsonPath("id").isNotEmpty())
				.andExpect(jsonPath("title").value(dto.getTitle())).andExpect(jsonPath("author").value(dto.getAuthor()))
				.andExpect(jsonPath("isbn").value(dto.getIsbn()));

	}

	@Test
	@DisplayName("Should generate a validation error when there is not enough data to create a book.")
	public void createInvalidBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(new BookDto());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);
//		
//		mvc.perform(request)
//		.andExpect(status().isBadRequest())
//		.andExpect(jsonPath("errors", hasSize(3)));

	}

//	@Test
//	@DisplayName("Should generate a validation error when trying to register a book with isbn already used by another.")
//	public void createBookWithDuplicatedIsbn() throws Exception {
//
//		BookDto dto = createNewBookDto();
//		String json = new ObjectMapper().writeValueAsString(dto);
//
//		String errorMessage = "Isbn is already in use.";
//
//		BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(errorMessage));
//
//		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
//				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json);
//
//		mvc.perform(request).andExpect(status().isBadRequest())
//				// .andExpect(jsonPath("errors", hasSize(1)))
//				.andExpect(jsonPath("errors[0]").value(errorMessage));
//
//	}

	@Test
	@DisplayName("Should get informations about a book")
	public void getBookDetailsTest() throws Exception {
		Long id = 1l;
		Book book = new Book(id, createNewBookDto().getTitle(), createNewBookDto().getAuthor(),
				createNewBookDto().getIsbn());
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isOk())
				.andExpect(jsonPath("title").value(createNewBookDto().getTitle()))
				.andExpect(jsonPath("author").value(createNewBookDto().getAuthor()))
				.andExpect(jsonPath("isbn").value(createNewBookDto().getIsbn())).andExpect(jsonPath("id").value(id));
	}

	@Test
	@DisplayName("Should return resource not found when a seached book does not exist")
	public void bookNotFoundTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(BOOK_API.concat("/" + 1l))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should delete a book")
	public void deleteBookTest() throws Exception {

		Long id = 1l;
		Book book = new Book(id, createNewBookDto().getTitle(), createNewBookDto().getAuthor(),
				createNewBookDto().getIsbn());
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + id))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNoContent());

	}

	@Test
	@DisplayName("Should return resource not found when a book to be deleted does not exist")
	public void deleteInexistBookTest() throws Exception {

		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + 1l))
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should update a book")
	public void updateBookTest() throws Exception {

		Long id = 1l;
		Book bookToBeUpdated = new Book(id, "Old Title", "Old author", createNewBookDto().getIsbn());
		Book book = new Book(id, "My book", "Author", "123456");

		String json = new ObjectMapper().writeValueAsString(createNewBookDto());

		BDDMockito.given(service.getById(id)).willReturn(Optional.of(bookToBeUpdated));
		BDDMockito.given(service.update(Mockito.any(Book.class))).willReturn(book);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + id)).content(json)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isOk())
				.andExpect(jsonPath("title").value(createNewBookDto().getTitle()))
				.andExpect(jsonPath("author").value(createNewBookDto().getAuthor()))
				.andExpect(jsonPath("isbn").value(bookToBeUpdated.getIsbn()))
				.andExpect(jsonPath("id").value(bookToBeUpdated.getId()));

	}

	@Test
	@DisplayName("Should return resource not found when a book to be updated does not exist")
	public void updateInexistBookTest() throws Exception {

		String json = new ObjectMapper().writeValueAsString(createNewBookDto());

		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(BOOK_API.concat("/" + 1l)).content(json)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isNotFound());

	}

	private BookDto createNewBookDto() {
		return new BookDto(1l, "My book", "Author", "123456");
	}
}
