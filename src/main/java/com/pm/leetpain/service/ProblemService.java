package com.pm.leetpain.service;

import com.pm.leetpain.Domain.PartialProblem;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.Repository.ProblemRepository;
import com.pm.leetpain.mapper.ProblemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
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

}
