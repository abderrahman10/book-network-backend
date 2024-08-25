package com.books.book.feedback;

import com.books.book.book.Book;
import com.books.book.book.BookRepository;
import com.books.book.common.PageResponse;
import com.books.book.handler.OperationNotPermittedException;
import com.books.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final BookRepository bookRepository;
    private final FeedBackMapper feedBackMapper;
    private final FeedBackRepository feedBackRepository;

    public Integer save(FeedBackRequest feedBackRequest, Authentication connectedUser) {

        Book book = bookRepository.findById(feedBackRequest.bookId()).orElseThrow(() -> new EntityNotFoundException("No book found with the Id :" + feedBackRequest.bookId()));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("you cannot give feedback to an archived or not shareable book");
        }
        User userconnected = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), userconnected.getId())) {
            throw new OperationNotPermittedException("you can not  five a feedback to your own book");
        }

        Feedback feedback = feedBackMapper.toFeedBack(feedBackRequest);
        return feedBackRepository.save(feedback).getId();
    }

    @Transactional
    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        User user = ((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks = feedBackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedBackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }



}