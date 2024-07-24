package com.books.book.book;

import com.books.book.common.PageResponse;
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

@Service
@RequiredArgsConstructor
public class BookService {
    private  final  BookMapper bookMapper;
    private  final  BookRepository bookRepository;
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
}
