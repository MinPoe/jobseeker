package com.board.jobseeker;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/// The JobEntry data type will store:
/// jobName - the name of the job position
/// companyName - the name of the company posting the job
/// postDate - the date at which the company posted the job
/// closeDate - the date at which the job application closes (OPTIONAL, some may not have)
/// jobLocation - job location
/// jobDuration - how long the job is (e.g. for internships), 0 for not specified (full-time)
/// jobType - "Internship", "Part-time", "Full-time" 
/// jobPay - compensation that the job provides per month (TENTATIVE)
/// jobLink - link to job application
public record JobEntry (String jobName, String companyName, LocalDate postDate, Optional<LocalDate> closeDate,
                        String jobLocation, int jobDuration, String jobType, int jobPay, String jobLink) {
    public JobEntry {
        Objects.requireNonNull(jobName);
        Objects.requireNonNull(companyName);
        Objects.requireNonNull(postDate);
        Objects.requireNonNull(jobLocation);
        Objects.requireNonNull(jobType);
        Objects.requireNonNull(jobLink);
    }
}
