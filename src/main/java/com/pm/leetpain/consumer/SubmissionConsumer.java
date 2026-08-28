package com.pm.leetpain.consumer;

import com.pm.leetpain.Domain.Submission;
import com.pm.leetpain.config.RabbitMQConfig;
import com.pm.leetpain.judge.RuntimeExecutor;
import com.pm.leetpain.service.ProblemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class SubmissionConsumer {
    private final RuntimeExecutor runtimeExecutor;
    private final ProblemService problemService;

    public SubmissionConsumer(RuntimeExecutor runtimeExecutor, ProblemService problemService) {
        this.runtimeExecutor = runtimeExecutor;
        this.problemService = problemService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleSubmission(Submission submission){
        log.debug("Received submission for problem id {}: {} at {}", submission.getProblemId(), submission , Instant.now());
        Submission postSubmission = problemService.submitSolution(submission.getProblemId(), submission.getSourceCode(), submission.getLanguage());
        log.debug("Processed submission for problem id {}: {} at {}", submission.getProblemId(), postSubmission , Instant.now());
        //save result to database
    }
}
