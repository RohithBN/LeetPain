package com.pm.leetpain.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Problem {

    private Long id;
    private String title;
    private String slug;
    private String description;
    private Difficulty difficulty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TestCase> testCases;

    private Map<Language, String> languageStubs;
    private Map<Language, String> driverHarnesses;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public Map<Language, String> getLanguageStubs() {
        return languageStubs;
    }

    public Map<Language, String> getDriverHarnesses() {
        return driverHarnesses;
    }

    public Problem() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public void setLanguageStubs(Map<Language, String> languageStubs) {
        this.languageStubs = languageStubs;
    }

    public void setDriverHarnesses(Map<Language, String> driverHarnesses) {
        this.driverHarnesses = driverHarnesses;
    }

    public enum Language {
        CPP,
        JAVA,
        PYTHON
    }


    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}