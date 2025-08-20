package com.board.jobseeker.jobs;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

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
@Entity
public class JobEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    // forces jobID --> job_id instead of jobid
    private Long jobID;

    private String jobName;
    private String companyName;
    private LocalDate postDate;
    private LocalDate closeDate;
    private String jobLocation;
    private int jobDuration;
    private String jobType;
    private int jobPay;
    private String jobLink;
    private String owner;

    public static final LocalDate NO_CLOSE_DATE = LocalDate.of(9999, 12, 31);

    public JobEntry() {}

    public JobEntry(String jobName, String companyName, LocalDate postDate, LocalDate closeDate, String jobLocation, int jobDuration, String jobType, int jobPay, String jobLink, Long jobID, String owner) {
        this.jobName = Objects.requireNonNull(jobName);
        this.companyName = Objects.requireNonNull(companyName);
        this.postDate = Objects.requireNonNull(postDate);
        this.closeDate = Objects.requireNonNull(closeDate);
        this.jobLocation = Objects.requireNonNull(jobLocation);
        this.jobDuration = jobDuration;
        this.jobType = Objects.requireNonNull(jobType);
        this.jobPay = jobPay;
        this.jobLink = Objects.requireNonNull(jobLink);
        this.jobID = jobID;
        this.owner = owner;
    }

    public Long getJobID() { return jobID; }
    public void setJobID(Long jobID) { this.jobID = jobID; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public LocalDate getPostDate() { return postDate; }
    public void setPostDate(LocalDate postDate) { this.postDate = postDate; }

    public LocalDate getCloseDate() { return closeDate; }
    public void setCloseDate(LocalDate closeDate) { this.closeDate = closeDate; }

    public String getJobLocation() { return jobLocation; }
    public void setJobLocation(String jobLocation) { this.jobLocation = jobLocation; }

    public int getJobDuration() { return jobDuration; }
    public void setJobDuration(int jobDuration) { this.jobDuration = jobDuration; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public int getJobPay() { return jobPay; }
    public void setJobPay(int jobPay) { this.jobPay = jobPay; }

    public String getJobLink() { return jobLink; }
    public void setJobLink(String jobLink) { this.jobLink = jobLink; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public boolean hasCloseDate() {
        return !NO_CLOSE_DATE.equals(closeDate);
    }
}
