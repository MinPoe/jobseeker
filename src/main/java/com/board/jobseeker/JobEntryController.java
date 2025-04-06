package com.board.jobseeker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // allow for HTTP request handling 
@RequestMapping("/jobseeker") // HTTP requests mapped to this are directed to this controller  
public class JobEntryController {

    /// handles requests mapped to /jobseeker/{requestedID} 
    /// returns: 
    ///     status - HTTP "200 OK"
    ///     response body - job entry data 
    @GetMapping("/{requestedID}")
    private ResponseEntity<String> findById(@PathVariable int requestedID) {
        // use path variable to match ID to data 
        return ResponseEntity.ok("");
    }
}
