package com.pm.leetpain.controller;

import com.pm.leetpain.Domain.PartialProblem;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.service.ProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<String> submitSolution(@PathVariable long id, String solution) {
        return ResponseEntity.ok("Solution submitted for problem " + id);
    }
}
