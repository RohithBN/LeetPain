package com.pm.leetpain.judge;

import org.springframework.stereotype.Component;

@Component
public class HarnessFactory {
    private final JavaHarness javaHarness;

    public HarnessFactory(JavaHarness javaHarness) {
        this.javaHarness = javaHarness;
    }

    public Harness getHarnessForLanguage(String language) {
        if (language == null) throw new IllegalArgumentException("language is null");
        String lang = language.trim().toLowerCase();
        switch (lang) {
            case "java":
            case "jav":
                return javaHarness;
            default:
                throw new UnsupportedOperationException("No harness for language: " + language);
        }
    }
}
