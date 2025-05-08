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

import java.time.LocalDate;
import java.net.URI;


@SpringBootTest(
	// start Spring boot application to allow for testing 
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class JobseekerAPITests {
	// dependency injection (autowired) for test helper to aid in HTTP request creation 
	@Autowired 
	TestRestTemplate restTemplate; 

	/// Request Type : GET 
	/// Description : given an existing job entry, should be able to request 'get' the entry (PASSING test)
	@Test
	void getAvailableJobEntry() {
		ResponseEntity<String> response = restTemplate.getForEntity("/jobseeker/11", String.class); 

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// take JSON of response body and map every field to entries 
		DocumentContext documentContext = JsonPath.parse(response.getBody()); 

		// create test variables 
		LocalDate correct_postDate = LocalDate.of(2025,9,30); 
		LocalDate correct_closeDate = LocalDate.of(2025,12,31); 

		// assert that data returned is expected, testing only unique types, not all
		assertThat(documentContext.read("$.jobID", Long.class)).isEqualTo(11L);
		assertThat(documentContext.read("$.jobName", String.class)).isEqualTo("Firmware Testing"); 

		LocalDate parsed_postDate = LocalDate.parse(documentContext.read("$.postDate",String.class));
		LocalDate parsed_closeDate = LocalDate.parse(documentContext.read("$.closeDate", String.class)); 

		assertThat(parsed_postDate.equals(correct_postDate)); 
		assertThat(parsed_closeDate.equals(correct_closeDate)); 
	}

	/// Request Type : GET 
	/// Description : when requested for an invalid job entry ID, should return HTTP status "404 NOT FOUND" (PASSING test)
	@Test
	void getUnknownJobEntry() {
		ResponseEntity<String> response = restTemplate.getForEntity("/jobseeker/0", String.class); 

		// assert that the response is 404 and returns an empty body 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
		assertThat(response.getBody()).isBlank(); 
	}

	/// Request Type : POST 
	/// Description : non-failing POST request to API, database should update 
	/// Expect : "201 CREATED", GET request to new resource location to be "200 OK" 
	@Test
	void createNewJobEntry() { 
		LocalDate postDateEntry = LocalDate.parse("2025-01-30");
		LocalDate closeDateEntry = LocalDate.parse("2025-05-30");
		JobEntry newEntry = new JobEntry("Marketing Intern", "Meta", postDateEntry, closeDateEntry, "Texas", 4, "Internship", 12000, "https://meta.com", null); 

		ResponseEntity<Void> responsePOST = restTemplate.postForEntity("/jobseeker", newEntry, Void.class); 

		assertThat(responsePOST.getStatusCode()).isEqualTo(HttpStatus.CREATED); 

		// the response should contain a header with the location of resource created 
		URI jobEntryLocation = responsePOST.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.getForEntity(jobEntryLocation, String.class); 

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK); 

	}
}
