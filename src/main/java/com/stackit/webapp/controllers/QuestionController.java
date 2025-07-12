package com.stackit.webapp.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stackit.webapp.model.Question;
import com.stackit.webapp.repositories.QuestionRepository;

@Controller
public class QuestionController {

    private final QuestionRepository questionRepo;

    public QuestionController(QuestionRepository questionRepo) {
        this.questionRepo = questionRepo;
    }

    // For /questions listing
    @GetMapping("/questions")
    public String listQuestions(
        @RequestParam(defaultValue = "0") int page,
        Model model
    ) {
        Page<Question> questions = questionRepo.findAll(PageRequest.of(page, 10));
        model.addAttribute("questions", questions);
        return "questions/list"; // will use layouts/base.html
    }

    // For preview on static index.html
    @GetMapping("/preview-questions")
    public String previewQuestions(Model model) {
        Page<Question> preview = questionRepo.findAll(PageRequest.of(0, 5));
        model.addAttribute("previewQuestions", preview);
        return "fragments/question-list :: questionList";
    }
}
