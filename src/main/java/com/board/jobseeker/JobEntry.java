package com.board.jobseeker;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

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
/// jobID - unique numeric identifier for each job 
/// owner - user account that posted the job, thereby having access to modifications

public record JobEntry (
    String jobName, 
    String companyName, 
    LocalDate postDate, 
    LocalDate closeDate,
    String jobLocation, 
    int jobDuration, 
    String jobType, 
    int jobPay, 
    String jobLink, 
    @Id Long jobID, 
    String owner) {
    
    // CONSTANT anomalous value for NO_CLOSE_DATE
    public static final LocalDate NO_CLOSE_DATE = LocalDate.of(9999, 12, 31); 

    public JobEntry {
        Objects.requireNonNull(jobName);
        Objects.requireNonNull(companyName);
        Objects.requireNonNull(postDate);
        Objects.requireNonNull(closeDate); 
        Objects.requireNonNull(jobLocation);
        Objects.requireNonNull(jobDuration);
        Objects.requireNonNull(jobType);
        Objects.requireNonNull(jobPay); 
        Objects.requireNonNull(jobLink);
    }

    // Helper method for checking closeDate is valid
    public boolean hasCloseDate() {
        return !NO_CLOSE_DATE.equals(closeDate); 
    }
}
