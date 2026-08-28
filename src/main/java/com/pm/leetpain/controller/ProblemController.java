package com.pm.leetpain.controller;

import com.pm.leetpain.Domain.PartialProblem;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.Domain.Submission;
import com.pm.leetpain.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/problem/{id}")
    public ResponseEntity<Problem> getProblemById(@PathVariable long id) {

        if(id <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
            return ResponseEntity.ok(problemService.getProblemById(id));
    }

    @GetMapping({"/problem", "/problems"})
    public ResponseEntity<List<PartialProblem>> getAllProblems() {
        return ResponseEntity.ok(problemService.getAllProblems());
    }

    @PostMapping("/problem/{id}/submit")
    public ResponseEntity<Submission> submitSolution(@PathVariable long id, @RequestBody SubmitRequest request) {
        log.debug("Received submission request for problem id {}: {}", id, request);
        if (request == null || request.language() == null || request.language().isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (request.solution() == null || request.solution().isBlank()) {
            return ResponseEntity.badRequest().body(null);
        }
        Submission submission = problemService.submitSolution(id, request.solution(), request.language());
        return ResponseEntity.ok(submission);
    }

    public record SubmitRequest(String solution, String language) {}
}
