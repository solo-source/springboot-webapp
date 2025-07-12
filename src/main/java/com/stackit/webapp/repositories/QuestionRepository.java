package com.stackit.webapp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stackit.webapp.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    /**
     * Finds all questions with pagination.
     * Spring Data JPA provides the implementation automatically.
     */
    @Override
    Page<Question> findAll(Pageable pageable);
}