package br.com.project.libraryapi.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.project.libraryapi.api.dto.BookDto;
import br.com.project.libraryapi.api.exception.ApiErrors;
import br.com.project.libraryapi.exception.BusinessException;
import br.com.project.libraryapi.model.entity.Book;
import br.com.project.libraryapi.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private BookService service;
	private ModelMapper modelMapper;

	public BookController(BookService service, ModelMapper mapper) {
		this.service = service;
		this.modelMapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDto create(@RequestBody @Valid BookDto dto) {
		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		return modelMapper.map(entity, BookDto.class);
	}

	@GetMapping("/{id}")
	public BookDto get(@PathVariable Long id) {
		return service.getById(id).map(book -> modelMapper.map(book, BookDto.class))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		service.delete(id);
	}
	
	@PutMapping("/{id}")
	public BookDto put(@PathVariable Long id, @RequestBody @Valid BookDto dto) {
		BookDto bookDto = service.getById(id).map(book -> modelMapper.map(book, BookDto.class))
		.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		bookDto.setAuthor(dto.getAuthor());
		bookDto.setTitle(dto.getTitle());
		
		Book entity = modelMapper.map(dto, Book.class);
		entity = service.update(entity);
		return modelMapper.map(entity, BookDto.class);
	}
	
	@GetMapping
	public Page<BookDto> find(BookDto dto, Pageable pageRequest) {
		Book filter = modelMapper.map(dto, Book.class);
		Page<Book> result = service.find(filter, pageRequest);
		
		List<BookDto> list = result.getContent().stream()
			.map( entity -> modelMapper.map(entity, BookDto.class))
			.collect( Collectors.toList());
		
		return new PageImpl<>(list, pageRequest, result.getTotalElements());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationExceptios(MethodArgumentNotValidException exception) {
		BindingResult bindingResult = exception.getBindingResult();

		return new ApiErrors(bindingResult);
	}

	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessException(BusinessException exception) {

		return new ApiErrors(exception);
	}

}
