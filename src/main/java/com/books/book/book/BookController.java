package com.books.book.book;

import com.books.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {
    private  final  BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> saveBook(@Valid @RequestBody BookRequest bookRequest){

      return ResponseEntity.ok(bookService.save(bookRequest));
    }

    @GetMapping("{book-id}")
    public  ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId){
        return  ResponseEntity.ok(bookService.findBookById(bookId));
    }
//all books except the connected user
    @GetMapping("find-all-books")
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

    @DeleteMapping("/delete/{book-id}")
    public ResponseEntity<Integer> DeleteBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.deleteBook(bookId,connectedUser));

    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> BorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.borrowBook(bookId,connectedUser));

    }
    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.returnBorrowedBook(bookId,connectedUser));

    }
    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> returnApproveBorrowBook(@PathVariable("book-id") Integer bookId, Authentication connectedUser){
        return  ResponseEntity.ok(bookService.approveReturnBorrowedBook(bookId,connectedUser));

    }
    @PatchMapping(value = "/cover/{book-id}",consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(@PathVariable("book-id") Integer bookId, @Parameter() @RequestPart("file") MultipartFile file, Authentication connectedUser){
       bookService.uploadBookCoverPicture(file,connectedUser,bookId);
        return  ResponseEntity.accepted().build();

    }
}
