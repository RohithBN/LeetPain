package com.pm.leetpain.Domain;

public class PartialProblem {

    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;


    public PartialProblem() {
    }

    public PartialProblem(Long id, String title, String description, Difficulty difficulty) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
    }

    public PartialProblem(Long id, String title, String description, Problem.Difficulty difficulty) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty == null ? null : Difficulty.valueOf(difficulty.name());
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }


    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}