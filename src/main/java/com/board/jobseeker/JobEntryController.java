package com.board.jobseeker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional; 

@RestController  // allow for HTTP request handling 
@RequestMapping("/jobseeker") // HTTP requests mapped to this are directed to this controller  
public class JobEntryController {

    // inject repository to manage JobEntry data 
    // actual SQL stored in ~src/main/resources/schema.sql
    private final JobEntryRepository jobEntryRepository; 

    /// handles requests mapped to /jobseeker/{requestedID} 
    /// returns: 
    ///     status - HTTP "200 OK"
    ///     response body - job entry data 
    @GetMapping("/{requestedID}")
    private ResponseEntity<JobEntry> findById(@PathVariable int requestedID) {
        // use path variable to match ID to data 
        Optional<JobEntry> jobEntry = jobEntryRepository.findById(requestedID); 
        
        if (jobEntry.isPresent()) {
            return ResponseEntity.ok(jobEntry.get()); 
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }   

    private JobEntryController(JobEntryRepository jobEntryRepository) {
        this.jobEntryRepository = jobEntryRepository; 
    }
}
