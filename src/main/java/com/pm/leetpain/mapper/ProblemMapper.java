package com.pm.leetpain.mapper;

import com.pm.leetpain.Domain.PartialProblem;
import com.pm.leetpain.Domain.Problem;

public class ProblemMapper {

    public static PartialProblem mapProblem(Problem problem){
        return new PartialProblem(problem.getId(), problem.getTitle(), problem.getDescription(), problem.getDifficulty());
    }
}
