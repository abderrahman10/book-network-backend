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

    @GetMapping("{book-id}")
    public  ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId){
        return  ResponseEntity.ok(bookService.findBookById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>>findAllBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ){
        return  ResponseEntity.ok(bookService.findAllBooks(page,size,connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>>findAllBookByOwner(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser){
        return  ResponseEntity.ok(bookService.findAllBooksByOwner(page,size,connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>>findAllBorrowedBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser){
        return  ResponseEntity.ok(bookService.findAllBorrowedBooks(page,size,connectedUser));
    }
    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>>findAllReturnedBooks(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser){
        return  ResponseEntity.ok(bookService.findAllReturnedBooks(page,size,connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
         return  ResponseEntity.ok(bookService.updateShareableStatus(bookId,connectedUser));

    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.updateArchivedStatus(bookId,connectedUser));

    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> BorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.borrowBook(bookId,connectedUser));

    }
    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.returnBorrowBook(bookId,connectedUser));

    }
    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> returnApproveBorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.returnApproveBorrowBook(bookId,connectedUser));

    }
}
