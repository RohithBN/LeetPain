package com.pm.leetpain.Domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestCase {

    private String input;

    private String expectedOutput;

    private boolean hidden;

    public String getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}