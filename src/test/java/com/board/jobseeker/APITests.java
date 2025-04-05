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

    // test correct SERIALIZATION (data stream --> object) of job object 
    @Test
    void jobEntrySerializationTest() throws IOException{
        JobEntry job = new JobEntry(
            "Software Developer Intern", "LinkedIn",
            LocalDate.of(2025,4,5),
            Optional.of(LocalDate.of(2025,4,20)),
            "Jakarta", 3,
            "Internship", 20,
            "https://linkedin.com");

        // takes 'job' object and writes it in json format, asserting to expected perfect outcome
        // NOTE: checks every field 
        assertThat(jsonContainer.write(job)).isStrictlyEqualToJson("expected.json");
    }

    // SERIALIZATION, test case 1: Optional closeDate empty (Passing Test)
    @Test
    void jobEntrySerializationTest1() throws IOException{
        JobEntry job = new JobEntry(
            "Software Developer Intern", "LinkedIn",
            LocalDate.of(2025,4,5),
            Optional.empty(),
            "Jakarta", 3,
            "Internship", 20,
            "https://linkedin.com");

        // check that closeDate fields is empty
        assertThat(jsonContainer.write(job)).hasEmptyJsonPathValue("@.closeDate"); 
    }

    // SERIALIZATION, test case 2: Data Mismatch (Failing Test)
    @Test
    void jobEntrySerializationTest2() throws IOException{
        JobEntry job = new JobEntry(
            "Software Developer Intern", "LinkedIn",
            LocalDate.of(2025,4,5),
            Optional.empty(),
            "Canada", 3,
            "Internship", 20,
            "https://linkedin.com");

        assertThat(jsonContainer.write(job)).extractingJsonPathStringValue("@.jobLocation").isNotEqualTo("Jakarta");
    }

    // test correct DESERIALIZATION (object --> data stream) of job object (JSON used for data stream)
    @Test 
    void jobEntryDeserializationTest() throws IOException{
        String expectedFields = """
                {
                    "jobName": "Marketing Intern",
                    "companyName": "JJ Cafe", 
                    "postDate": "2025-04-20",
                    "closeDate": "2025-04-30",
                    "jobLocation": "Burnaby",
                    "jobDuration": 4, 
                    "jobType": "Internship",
                    "jobPay": 24, 
                    "jobLink": "https://jj-cafe-jobs.com"
                }
                """;

        assertThat(jsonContainer.parse(expectedFields))
            .isEqualTo(new JobEntry("Marketing Intern", "JJ Cafe", 
                                    LocalDate.of(2025,4,20), Optional.of(LocalDate.of(2025,4,30)), 
                                    "Burnaby", 4, "Internship",
                                    24, "https://jj-cafe-jobs.com"
                                    ));
    }

    // DESERIALIZATION, test case 1: failing test, jobPay mismatch 
    @Test
    void jobEntryDeserializationTest1() throws IOException{
        String expectedFields = """
                {
                    "jobName": "Marketing Intern",
                    "companyName": "JJ Cafe", 
                    "postDate": "2025-04-20",
                    "closeDate": "2025-04-30",
                    "jobLocation": "Burnaby",
                    "jobDuration": 4, 
                    "jobType": "Internship",
                    "jobPay": 24, 
                    "jobLink": "https://jj-cafe-jobs.com"
                }
                """;

        assertThat(jsonContainer.parse(expectedFields))
            .isNotEqualTo(new JobEntry("Marketing Intern", "JJ Cafe", 
                                    LocalDate.of(2025,4,20), Optional.of(LocalDate.of(2025,4,30)), 
                                    "Burnaby", 4, "Internship",
                                    23, "https://jj-cafe-jobs.com"
                                    ));
    }
}
