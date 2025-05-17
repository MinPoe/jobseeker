package com.board.jobseeker;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

// extend sub-interface of Spring Data's |Repository|, automatically generating CRUD methods
// CrudRepository<JobEntry, Long> indicates to repository that domain type is JobEntry, with it's ID being a Long 
interface JobEntryRepository extends CrudRepository<JobEntry, Long>, PagingAndSortingRepository<JobEntry, Long> {
    
    // returns a specific job entry given jobID and owner, used for requests that require authorization 
    JobEntry findByJobIDAndOwner(Long jobID, String owner); 

    boolean existsByJobIDAndOwner(Long jobID, String owner); 
}
