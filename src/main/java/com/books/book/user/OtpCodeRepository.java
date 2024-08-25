package com.books.book.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode,Integer> {

    Optional<OtpCode> findByToken(String token);

}
