package com.books.book.feedback;


import com.books.book.book.Book;
import com.books.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class Feedback extends BaseEntity {

    private  Double note;
    private  String comment;

    @ManyToOne
    @JoinColumn(name ="book_id" )
    private Book book;


}
