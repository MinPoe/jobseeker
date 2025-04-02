package com.board.jobseeker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@JsonTest
class APITests {
    @Autowired
    private JacksonTester<JobEntry> jsonContainer;

    @Test
    void jobEntrySerializationTest() throws IOException{
        JobEntry job = new JobEntry(
            "Software Developer Intern", "LinkedIn",
            LocalDate.of(2025,4,5),
            Optional.of(LocalDate.of(2025,4,20)),
            "Jakarta", 3,
            "Internship", 20,
            "https://linkedin.com");
    }
}
