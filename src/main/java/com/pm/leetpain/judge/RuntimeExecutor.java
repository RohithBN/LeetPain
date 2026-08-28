package com.pm.leetpain.judge;

import com.pm.leetpain.Domain.ExecutionResult;
import com.pm.leetpain.Domain.Problem;

public interface RuntimeExecutor {
    ExecutionResult execute(String code , String language , Problem problem);
}
