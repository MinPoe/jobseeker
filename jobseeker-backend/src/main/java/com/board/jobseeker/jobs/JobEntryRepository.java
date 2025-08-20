package com.board.jobseeker.jobs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// extend sub-interface of Spring Data's |Repository|, automatically generating CRUD methods
// CrudRepository<JobEntry, Long> indicates to repository that domain type is JobEntry, with it's ID being a Long 
@Repository
public interface JobEntryRepository extends JpaRepository<JobEntry, Long> {
    
    // returns a specific job entry given jobID and owner, used for requests that require authorization 
    JobEntry findByJobIDAndOwner(Long jobID, String owner); 

    boolean existsByJobIDAndOwner(Long jobID, String owner); 

}
