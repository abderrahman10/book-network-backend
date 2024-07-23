package com.books.book.book;

import com.books.book.common.BaseEntity;
import com.books.book.feedback.Feedback;
import com.books.book.history.BookTransactionHistory;
import com.books.book.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class Book extends BaseEntity {


    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;


    @ManyToOne
    @JoinColumn(name = "owner_id") //this annotation is optional
    private User owner;

    @OneToMany(mappedBy = "book")
    private List <Feedback> feedbacks;

   @OneToMany(mappedBy = "book")
   private List<BookTransactionHistory>bookTransactionHistories;


}