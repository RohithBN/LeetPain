package com.pm.leetpain.service;

import com.pm.leetpain.Domain.ExecutionResult;
import com.pm.leetpain.Domain.PartialProblem;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.Domain.Submission;
import com.pm.leetpain.Repository.ProblemRepository;
import com.pm.leetpain.judge.RuntimeExecutor;
import com.pm.leetpain.mapper.ProblemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {
    private static final Logger log = LoggerFactory.getLogger(ProblemService.class);

    private final ProblemRepository problemRepository;
    private final RuntimeExecutor runtimeExecutor;

    public ProblemService(ProblemRepository problemRepository, RuntimeExecutor runtimeExecutor) {
        this.problemRepository = problemRepository;
        this.runtimeExecutor = runtimeExecutor;
    }

    public Problem getProblemById(long id){
           return problemRepository.findById(id)
                   .orElse(null);

    }

    public List<PartialProblem> getAllProblems() {
        return problemRepository.findAll()
                .stream()
                .map(ProblemMapper::mapProblem)
                .toList();
    }

    public Problem saveProblem(Problem problem) {
        try {
            return problemRepository.save(problem);
        } catch (Exception e) {
            log.error("Failed to save problem: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Submission submitSolution(long problemId , String solution , String language){
        // get the problem
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));
        // execute
        try {
            ExecutionResult result = runtimeExecutor.execute(solution, language, problem);
            Submission submission = new Submission(problemId, mapResultToStatus(result));
            return submission;
        } catch (Exception e) {
           log.error("Error executing solution for problem id {}: {}", problemId, e.getMessage());
            Submission submission = new Submission(problemId, Submission.Status.COMPILATION_ERROR);
            return submission;

        }
    }

    private Submission.Status mapResultToStatus(ExecutionResult result) {
        if (result == null) return Submission.Status.RUNTIME_ERROR;
        if (result.getCompileError() != null) {
            String ce = result.getCompileError();
            if ("COMPILATION_ERROR".equals(ce)) return Submission.Status.COMPILATION_ERROR;
            if ("TIME_LIMIT_EXCEEDED".equals(ce)) return Submission.Status.TIME_LIMIT_EXCEEDED;
            return Submission.Status.RUNTIME_ERROR;
        }
        String status = result.getStatus();
        if (status == null) return Submission.Status.RUNTIME_ERROR;
        return switch (status) {
            case "ACCEPTED" -> Submission.Status.ACCEPTED;
            case "WRONG_ANSWER" -> Submission.Status.WRONG_ANSWER;
            default -> Submission.Status.RUNTIME_ERROR;
        };
    }


}
