package com.pm.leetpain.service;

import com.pm.leetpain.Domain.ExecutionResult;
import com.pm.leetpain.Domain.PartialProblem;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.Domain.Submission;
import com.pm.leetpain.Repository.ProblemRepository;
import com.pm.leetpain.judge.JavaRuntimeExecutor;
import com.pm.leetpain.judge.RuntimeExecutor;
import com.pm.leetpain.mapper.ProblemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final JavaRuntimeExecutor javaRuntimeExecutor;

    public ProblemService(ProblemRepository problemRepository, JavaRuntimeExecutor javaRuntimeExecutor) {
        this.problemRepository = problemRepository;
        this.javaRuntimeExecutor = javaRuntimeExecutor;
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

    public Submission submitSolution(long problemId , String solution , String language){
        // get the problem
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("Problem not found"));
        // create a new submission
        RuntimeExecutor runtimeExecutor = getRuntimeExecutor(language);
        ExecutionResult result = runtimeExecutor.execute(solution, language, problem);
        Submission submission = new Submission(problemId, result.getStderr().isEmpty()? Submission.Status.ACCEPTED: Submission.Status.COMPILATION_ERROR);
        return submission;
    }


    public RuntimeExecutor getRuntimeExecutor(String language) {
        return switch (language.toLowerCase()) {
            case "java" -> javaRuntimeExecutor;
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }

}
