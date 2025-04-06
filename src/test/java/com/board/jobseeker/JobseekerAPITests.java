package com.board.jobseeker;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
	// start Spring boot application to allow for testing 
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobseekerAPITests {
	// dependency injection (autowired) for test helper to aid in HTTP request creation 
	@Autowired 
	TestRestTemplate restTemplate; 

	// given an existing job entry, should be able to request 'get' the entry (PASSING test)
	@Test
	void getAvailableJobEntry() {
		ResponseEntity<String> response = restTemplate.getForEntity("/jobseeker/2", String.class); 

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// take JSON of response body, and parse to extract jobID field
		DocumentContext documentContext = JsonPath.parse(response.getBody()); 
		int id = documentContext.read("@.jobID"); 
		// assert that ID returned is expected
		assertThat(id).isEqualTo(2); 
	}

	// when requested for an invalid job entry ID, should return HTTP status "404 NOT FOUND" (PASSING test)
	@Test
	void getUnknownJobEntry() {
		ResponseEntity<String> response = restTemplate.getForEntity("/jobseeker/99999999000010101010", String.class); 

		// assert that the response is 404 and returns an empty body 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
		assertThat(response.getBody()).isBlank(); 
	}

}
