package com.pm.leetpain.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.leetpain.Domain.Problem;
import com.pm.leetpain.Domain.TestCase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProblemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ProblemRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<Problem> findById(long id) {

        String query = """
                SELECT id,
                       title,
                       slug,
                       description,
                       difficulty,
                       test_cases,
                       language_stubs,
                       created_at,
                       updated_at
                FROM problems
                WHERE id = ?
                """;

        return jdbcTemplate.query(
                query,
                this::mapProblem,
                id
        ).stream().findFirst();
    }

    public List<Problem> findAll() {

        String query = """
                SELECT id,
                       title,
                       slug,
                       description,
                       difficulty,
                       test_cases,
                       language_stubs,
                       created_at,
                       updated_at
                FROM problems
                ORDER BY id
                """;

        return jdbcTemplate.query(query, this::mapProblem);
    }

    private Problem mapProblem(ResultSet rs, int rowNum)
            throws SQLException {

        try {
            List<TestCase> testCases =
                    objectMapper.readValue(
                            rs.getString("test_cases"),
                            new TypeReference<List<TestCase>>() {}
                    );

            Map<Problem.Language, String> languageStubs =
                    objectMapper.readValue(
                            rs.getString("language_stubs"),
                            new TypeReference<Map<Problem.Language, String>>() {}
                    );

            Problem problem = new Problem();
            problem.setId(rs.getLong("id"));
            problem.setTitle(rs.getString("title"));
            problem.setSlug(rs.getString("slug"));
            problem.setDescription(rs.getString("description"));
            problem.setDifficulty(
                    Problem.Difficulty.valueOf(
                            rs.getString("difficulty")
                    )
            );
            problem.setTestCases(testCases);
            problem.setLanguageStubs(languageStubs);
            problem.setCreatedAt(
                    rs.getTimestamp("created_at")
                            .toLocalDateTime()
            );
            problem.setUpdatedAt(
                    rs.getTimestamp("updated_at")
                            .toLocalDateTime()
            );
            return problem;

        } catch (JsonProcessingException e) {
            throw new SQLException(
                    "Failed to parse problem JSON",
                    e
            );
        }
    }
}