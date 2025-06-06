package com.board.jobseeker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional; 
import java.net.URI;
import java.security.Principal;

import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// JSON imports
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

// LocalDate handling 
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;


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
    ///     handles POST requests mapped to /jobseeker, created job entry's ownership goes to user who posted it 
    /// returns: 
    ///     status - HTTP "201 CREATED"
    ///     response body - location header field of resource created 
    @PostMapping
    private ResponseEntity<Void> createJobEntry(@RequestBody JobEntry createdEntry, UriComponentsBuilder ucb, Principal principal) { 
        JobEntry jobEntry_withOwner = new JobEntry(createdEntry.jobName(), createdEntry.companyName(), createdEntry.postDate(), createdEntry.closeDate(), createdEntry.jobLocation(), createdEntry.jobDuration(), createdEntry.jobType(), createdEntry.jobPay(), createdEntry.jobLink(), null, principal.getName()); 
        JobEntry postedEntry = jobEntryRepository.save(jobEntry_withOwner); 

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

    /// Request Type : PUT 
    ///     handles PUT requests mapped to /jobseeker/{requestedID}
    /// returns: 
    ///     status - HTTP "204 NO_CONTENT"
    ///     response body - empty 
    @PutMapping("/{requestedID}")
    private ResponseEntity<Void> putJobEntry(@PathVariable Long requestedID, @RequestBody JobEntry update, Principal principal) {
        JobEntry jobEntry = jobEntryRepository.findByJobIDAndOwner(requestedID, principal.getName()); 

        if (jobEntry != null) {
            JobEntry updatedJobEntry = new JobEntry(update.jobName(), update.companyName(), update.postDate(), update.closeDate(), update.jobLocation(), update.jobDuration(), update.jobType(), update.jobPay(), update.jobLink(), jobEntry.jobID(), principal.getName()); 
            jobEntryRepository.save(updatedJobEntry); 

            return ResponseEntity.noContent().build();
        }

        else { 
            // upon non-existent IDs or unauthorized PUT requests, return ambiguous "404 NOT FOUND" to conceal information 
            return ResponseEntity.notFound().build(); 
        }
    }

    /// Request Type : PATCH with JSON-Patch format 
    ///     handles PATCH requests mapped to /jobseeker/{requestedID}
    /// returns: 
    ///     status - HTTP "204 NO_CONTENT"
    ///     response body - empty 
    @PatchMapping(path = "/{requestedID}", consumes ="application/json-patch+json")
    private ResponseEntity<Void> patchJobEntry(@PathVariable Long requestedID, @RequestBody JsonPatch patch, Principal principal) {
        try {
            JobEntry jobEntry = jobEntryRepository.findByJobIDAndOwner(requestedID, principal.getName()); 
            JobEntry jobPatched = applyPatchToJob(patch, jobEntry);
            jobEntryRepository.save(jobPatched); 
            return ResponseEntity.noContent().build(); 
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // TODO: maybe change to different error code for ambiguity? 
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /// Helper Method
    ///     applies PATCHES to job entries 
    public JobEntry applyPatchToJob(JsonPatch patch, JobEntry job) throws JsonPatchException, JsonProcessingException {
        // allow conversion of object to JSON 
        ObjectMapper objectMapper = new ObjectMapper();
        // proper handling of LocalDate fields 
        objectMapper.registerModule(new JavaTimeModule());
        // keep dates in same format, instead of timestamps 
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // convert JobEntry job object to JSON
        JsonNode originalNode = objectMapper.convertValue(job, JsonNode.class);
        // traverse originalNode tree using patch path and apply operations 
        JsonNode patchedNode = patch.apply(originalNode);
        // convert JSON back to JobEntry type 
        JobEntry result = objectMapper.treeToValue(patchedNode, JobEntry.class);

        return result;
    }

    /// Request Type : DELETE
    ///     handles HARD DELETE requests mapped to /jobseeker/{requestedID}, permanently deleting job entries
    /// returns: 
    ///     status - HTTP "204 NO_CONTENT"
    ///     response body - empty 
    /// NOTE: 
    ///     quirk - if non-registered user (random username and password) requests unauthorized DELETE, it shows "UNAUTHORIZED" instead of "NOT FOUND"
    @DeleteMapping("/{requestedID}") 
    private ResponseEntity<Void> deleteJobEntry(@PathVariable Long requestedID, Principal principal) {
        if (jobEntryRepository.existsByJobIDAndOwner(requestedID, principal.getName())) {
            jobEntryRepository.deleteById(requestedID);
            return ResponseEntity.noContent().build(); 
        }

        return ResponseEntity.notFound().build(); 
    }


}
