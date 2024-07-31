package com.books.book.feedback;

import com.books.book.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedBackMapper {

    public Feedback toFeedBack(FeedBackRequest feedBackRequest) {
        return  Feedback.builder()
                .note(feedBackRequest.note())
                .comment(feedBackRequest.comment())
                .book(Book
                        .builder()
                        .id(feedBackRequest.bookId())
                        .archived(false)
                        .shareable(false)
                        .build()
                )
                .build();
    }
}
