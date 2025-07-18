package com.board.jobseeker;

import org.assertj.core.util.Arrays; 
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.time.LocalDate;

@JsonTest
class JsonTests {
    @Autowired
    private JacksonTester<JobEntry> jsonContainer;

    @Autowired 
    private JacksonTester<JobEntry[]> jsonList; 

    private JobEntry[] jobs; 

    @BeforeEach 
    void setUp() {
        LocalDate postDate_1 = LocalDate.parse("2025-08-20"); 
        LocalDate closeDate_1 = LocalDate.parse("2025-12-20"); 

        LocalDate postDate_2 = LocalDate.parse("2025-09-30");
        LocalDate closeDate_2 = LocalDate.parse("2025-12-31"); 

        LocalDate postDate_3 = LocalDate.parse("2025-04-30"); 
        LocalDate closeDate_3 = LocalDate.parse("2025-08-30");

        jobs = Arrays.array(
            new JobEntry("Software Engineering", "LinkedIn",postDate_1 ,closeDate_1,
                        "Seattle", 4, "Internship", 3000, "https://linkedin.com", 20L, "miles1"), 

            new JobEntry("Firmware Testing", "Nvidia", postDate_2,closeDate_2,
                        "Santa Clara", 3, "Internship", 4000, "https://nvidia.careers.com", 21L, "liam"),

            new JobEntry("Hardware Testing", "Intel", postDate_3, closeDate_3,
                        "Vancouver", 4, "Part-Time", 5000, "https://intel.careers.com", 22L, "peter2") 
        );
    }


    // test correct SERIALIZATION (data stream --> object) of job object (PASSING test)
    @Test
    void jobEntrySerializationTest() throws IOException {
        JobEntry job = new JobEntry(
            "Software Developer Intern", "LinkedIn",
            LocalDate.of(2025,4,5),
            LocalDate.of(2025,4,20),
            "Jakarta", 3,
            "Internship", 20,
            "https://linkedin.com", 10L, "james");

        // takes 'job' object and writes it in json format, asserting to expected perfect outcome
        // NOTE: checks every field 
        assertThat(jsonContainer.write(job)).isStrictlyEqualToJson("expected.json");
    }

    // SERIALIZATION, test case 1: Optional closeDate empty (PASSING Test)
    @Test
    void jobEntrySerializationTest1() throws IOException {
        JobEntry job = new JobEntry(
            "Software Developer Intern", "LinkedIn",
            LocalDate.of(2025,4,5),
            JobEntry.NO_CLOSE_DATE,
            "Jakarta", 3,
            "Internship", 20,
            "https://linkedin.com", 10L, "james");

        // check that closeDate fields is empty
        assertThat(jsonContainer.write(job)).extractingJsonPathStringValue("@.closeDate").isEqualTo(JobEntry.NO_CLOSE_DATE.toString()); 
    }

    // SERIALIZATION, test case 2: Data Mismatch (FAILING Test)
    @Test
    void jobEntrySerializationTest2() throws IOException {
        JobEntry job = new JobEntry(
            "Software Developer Intern", "LinkedIn",
            LocalDate.of(2025,4,5),
            JobEntry.NO_CLOSE_DATE,
            "Canada", 3,
            "Internship", 20,
            "https://linkedin.com", 10L, "james");

        assertThat(jsonContainer.write(job)).extractingJsonPathStringValue("@.jobLocation").isNotEqualTo("Jakarta");
    }

    // test correct DESERIALIZATION (object --> data stream) of job object (JSON used for data stream) (PASSING test)
    @Test 
    void jobEntryDeserializationTest() throws IOException {
        String expectedFields = """
                {
                    "jobName": "Marketing Intern",
                    "companyName": "Uno Cafe", 
                    "postDate": "2025-04-20",
                    "closeDate": "2025-04-30",
                    "jobLocation": "Burnaby",
                    "jobDuration": 4, 
                    "jobType": "Internship",
                    "jobPay": 24, 
                    "jobLink": "https://jj-cafe-jobs.com",
                    "jobID": 11, 
                    "owner": "sarah"
                }
                """;

        assertThat(jsonContainer.parse(expectedFields))
            .isEqualTo(new JobEntry("Marketing Intern", "Uno Cafe", 
                                    LocalDate.of(2025,4,20), LocalDate.of(2025,4,30), 
                                    "Burnaby", 4, "Internship",
                                    24, "https://jj-cafe-jobs.com", 11L, "sarah"
                                    ));
    }

    // DESERIALIZATION, test case 1: jobPay mismatch (FAILING test) 
    @Test
    void jobEntryDeserializationTest1() throws IOException {
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
                    "jobLink": "https://jj-cafe-jobs.com",
                    "jobID": 11,
                    "owner": "sarah"
                }
                """;

        assertThat(jsonContainer.parse(expectedFields))
            .isNotEqualTo(new JobEntry("Marketing Intern", "JJ Cafe", 
                                    LocalDate.of(2025,4,20), LocalDate.of(2025,4,30), 
                                    "Burnaby", 4, "Internship",
                                    23, "https://jj-cafe-jobs.com", 11L, "sarah"
                                    ));
    }

    // LISTS
    // test correct SERIALIZATION 
    @Test
    void jobEntryListSerializationTest() throws IOException {

        assertThat(jsonList.write(jobs)).isStrictlyEqualToJson("list.json"); 
    }


    // test correct DESERIALIZATION 
    @Test
    void jobEntryListDeserializationTest() throws IOException {
        String expectedList = """
                [
                    {
                        "jobName": "Software Engineering",
                        "companyName": "LinkedIn",
                        "postDate": "2025-08-20",
                        "closeDate": "2025-12-20",
                        "jobLocation": "Seattle",
                        "jobDuration": 4,
                        "jobType": "Internship",
                        "jobPay": 3000,
                        "jobLink": "https://linkedin.com",
                        "jobID": 20, 
                        "owner": "miles1"
                    },

                    {
                        "jobName": "Firmware Testing",
                        "companyName": "Nvidia",
                        "postDate": "2025-09-30",
                        "closeDate": "2025-12-31",
                        "jobLocation": "Santa Clara",
                        "jobDuration": 3,
                        "jobType": "Internship",
                        "jobPay": 4000,
                        "jobLink": "https://nvidia.careers.com",
                        "jobID": 21,
                        "owner": "liam"
                    },

                    {
                        "jobName": "Hardware Testing",
                        "companyName": "Intel",
                        "postDate": "2025-04-30",
                        "closeDate": "2025-08-30",
                        "jobLocation": "Vancouver",
                        "jobDuration": 4,
                        "jobType": "Part-Time",
                        "jobPay": 5000,
                        "jobLink": "https://intel.careers.com",
                        "jobID": 22,
                        "owner": "peter2" 
                    }
                ]
                """;

                assertThat(jsonList.parse(expectedList)).isEqualTo(jobs); 
    }
}
