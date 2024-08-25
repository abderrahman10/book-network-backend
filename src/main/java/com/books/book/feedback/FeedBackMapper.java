package com.books.book.feedback;

import com.books.book.book.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}
