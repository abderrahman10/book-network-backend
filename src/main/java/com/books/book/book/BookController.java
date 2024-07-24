package com.books.book.book;

import com.books.book.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    private  final  BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> saveBook(@Valid @RequestBody BookRequest bookRequest, Authentication connectedUser){

      return ResponseEntity.ok(bookService.save(bookRequest,connectedUser));
    }

    @GetMapping("{bookId}")
    public  ResponseEntity<BookResponse> findBookById(@PathVariable("bookId") Integer bookId){
        return  ResponseEntity.ok(bookService.findBookById(bookId));
    }

    @GetMapping()
    public ResponseEntity<PageResponse<BookResponse>>findAllBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){
        return  ResponseEntity.ok(bookService.findAllBooks(page,size,connectedUser));
    }
}
