package com.books.book.book;

import com.books.book.common.PageResponse;
import com.books.book.handler.OperationNotPermittedException;
import com.books.book.history.BookTransactionHistory;
import com.books.book.history.BookTransactionHistoryRepository;
import com.books.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {
    private  final  BookMapper bookMapper;
    private  final  BookRepository bookRepository;
    private  final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    public Integer save(BookRequest bookRequest, Authentication connectedUser) {

     //Cette conversion fonctionne uniquement si l'objet renvoyé par getPrincipal()est effectivement une instance de User.
        User userconnected=(User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(userconnected);
        return bookRepository.save(book).getId();
    }

    public BookResponse findBookById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(()-> new EntityNotFoundException("No book found with the Id :" + id));
    }


    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User userconnected=(User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Book>books=bookRepository.findAllDisplayableBooks(pageable,userconnected.getId());
        List<BookResponse>bookResponses=books.stream().map(bookMapper::toBookResponse).toList();
        return  new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User userconnected=(User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Book>books=bookRepository.findAll(BookSpecification.withOwnerId(userconnected.getId()),pageable);
        List<BookResponse>bookResponses=books.stream().map(bookMapper::toBookResponse).toList();
        return  new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }


    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User userconnected=(User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory>allBorrowedbooks=bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,userconnected.getId());
         List<BorrowedBookResponse> borrowedBookResponses=allBorrowedbooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return  new PageResponse<>(
                borrowedBookResponses,
                allBorrowedbooks.getNumber(),
                allBorrowedbooks.getSize(),
                allBorrowedbooks.getTotalElements(),
                allBorrowedbooks.getTotalPages(),
                allBorrowedbooks.isFirst(),
                allBorrowedbooks.isLast()
        );

    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User userconnected=(User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory>allBorrowedbooks=bookTransactionHistoryRepository.findAllReturnedBooks(pageable,userconnected.getId());
        List<BorrowedBookResponse> borrowedBookResponses=allBorrowedbooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return  new PageResponse<>(
                borrowedBookResponses,
                allBorrowedbooks.getNumber(),
                allBorrowedbooks.getSize(),
                allBorrowedbooks.getTotalElements(),
                allBorrowedbooks.getTotalPages(),
                allBorrowedbooks.isFirst(),
                allBorrowedbooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId) .orElseThrow(()-> new EntityNotFoundException("No book found with the Id :" + bookId));
        User userconnected=(User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(),userconnected.getId())){
           throw  new OperationNotPermittedException("you can not update books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return  bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId) .orElseThrow(()-> new EntityNotFoundException("No book found with the Id :" + bookId));
        User userconnected=(User) connectedUser.getPrincipal();
        if(!Objects.equals(book.getOwner().getId(),userconnected.getId())){
            throw  new OperationNotPermittedException("you can not update books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return  bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId) .orElseThrow(()-> new EntityNotFoundException("No book found with the Id :" + bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("the requested book cannot be borrowed");
        }
        User userconnected=(User) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(),userconnected.getId())){
            throw  new OperationNotPermittedException("you can not  borrow your own book");
        }
        final boolean isAlreadyBorrowed= bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId,userconnected.getId());
        if(isAlreadyBorrowed){
            throw new OperationNotPermittedException("the book is already Borrowed");
        }
     BookTransactionHistory bookTransactionHistory=BookTransactionHistory.builder()
             .user(userconnected)
             .book(book)
             .returned(false)
             .returnApproved(false)
             .build();

        return  bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId) .orElseThrow(()-> new EntityNotFoundException("No book found with the Id :" + bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("the requested book cannot be borrowed");
        }
        User userconnected=(User) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(),userconnected.getId())){
            throw  new OperationNotPermittedException("you can not  returned your own book");
        }
        BookTransactionHistory bookTransactionHistory=bookTransactionHistoryRepository.findByBookIdAndUserId(bookId,userconnected.getId())
                .orElseThrow(()-> new OperationNotPermittedException("you did not borrow this book"));
       bookTransactionHistory.setReturned(true);
       return  bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnApproveBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book=bookRepository.findById(bookId) .orElseThrow(()-> new EntityNotFoundException("No book found with the Id :" + bookId));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("the requested book cannot be borrowed");
        }
        User userconnected=(User) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(),userconnected.getId())){
            throw  new OperationNotPermittedException("you can not  returned your own book");
        }
        BookTransactionHistory bookTransactionHistory=bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId,userconnected.getId())
                .orElseThrow(()-> new OperationNotPermittedException("this nook is not returned yet . you cannot approve its return"));
        bookTransactionHistory.setReturnApproved(true);
        return  bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }
}
