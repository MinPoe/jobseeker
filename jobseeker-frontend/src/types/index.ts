export interface JobEntry {
    jobName: string;
    companyName: string; 
    postDate: string; 
    closeDate: string; 
    jobLocation: string; 
    jobDuration: number;
    jobType: string; 
    jobPay: number; 
    jobLink: string; 
    jobID: number; 
    owner: string;  
}

export interface SearchFilters { 
    keyword: string; 
    location: string; 
    jobType: string; 
}

export interface LoginCredentials {
    username: string; 
    password: string; 
}