package com.board.jobseeker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional; 
import java.net.URI;

import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController  // allow for HTTP request handling 
@RequestMapping("/jobseeker") // HTTP requests mapped to this are directed to this controller  
public class JobEntryController {

    // inject repository to manage JobEntry data 
    // actual SQL stored in ~src/main/resources/schema.sql  
    private final JobEntryRepository jobEntryRepository; 

    private JobEntryController(JobEntryRepository jobEntryRepository) {
        this.jobEntryRepository = jobEntryRepository; 
    }

    /// Request Type : GET 
    ///     handles GET requests mapped to /jobseeker, with default sort of jobID in ascending order
    /// returns: 
    ///     status - HTTP "200 OK"
    ///     response body - list of job entry data with pagination 
    @GetMapping
    private ResponseEntity<List<JobEntry>> findAll(Pageable pageable) {
        Page<JobEntry> page = jobEntryRepository.findAll(
                PageRequest.of(
                    pageable.getPageNumber(), 
                    pageable.getPageSize(),
                    pageable.getSortOr(Sort.by(Sort.Direction.ASC, "jobID"))
        ));

        return ResponseEntity.ok(page.getContent());
    }

    /// Request Type : POST 
    ///     handles POST requests mapped to /jobseeker
    /// returns: 
    ///     status - HTTP "201 CREATED"
    ///     response body - location header field of resource created 
    @PostMapping
    private ResponseEntity<Void> createJobEntry(@RequestBody JobEntry createdEntry, UriComponentsBuilder ucb) { 
        JobEntry postedEntry = jobEntryRepository.save(createdEntry); 

        URI postLocation = ucb
                .path("jobseeker/{jobID}")
                .buildAndExpand(postedEntry.jobID())
                .toUri();
        return ResponseEntity.created(postLocation).build();
    }
    
    
    /// Request Type : GET 
    ///     handles GET requests mapped to /jobseeker/{requestedID} 
    /// returns: 
    ///     status - HTTP "200 OK"
    ///     response body - job entry data 
    @GetMapping("/{requestedID}")
    private ResponseEntity<JobEntry> findById(@PathVariable Long requestedID) {
        // use path variable to match ID to data 
        Optional<JobEntry> jobEntry = jobEntryRepository.findById(requestedID); 
        
        if (jobEntry.isPresent()) {
            return ResponseEntity.ok(jobEntry.get()); 
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }   

}
