package com.board.jobseeker;

import org.springframework.data.repository.CrudRepository;

// extend sub-interface of Spring Data's |Repository|, automatically generating CRUD methods
// CrudRepository<JobEntry, Long> indicates to repository that domain type is JobEntry, with it's ID being a Long 
interface JobEntryRepository extends CrudRepository<JobEntry, Long> {

}
