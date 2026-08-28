package com.pm.leetpain.controller;

import com.pm.leetpain.Domain.PartialProblem;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.Domain.Submission;
import com.pm.leetpain.config.RabbitMQConfig;
import com.pm.leetpain.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@RestController
public class ProblemController {
    private final ProblemService problemService;
    private final RabbitTemplate rabbitTemplate;

    public ProblemController(ProblemService problemService, RabbitTemplate rabbitTemplate) {
        this.problemService = problemService;
        this.rabbitTemplate = rabbitTemplate;
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
        Random random = new Random();

        // Generates a random long between Long.MIN_VALUE and Long.MAX_VALUE
        long submissionId = random.nextLong();


        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, new Submission(submissionId,id, request.language(), request.solution()));
        Submission response = new Submission(id, Submission.Status.QUEUED);
        return ResponseEntity.ok(response);
    }

    public record SubmitRequest(String solution, String language) {}
}
