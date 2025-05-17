package com.board.jobseeker;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpEntity; 
import org.springframework.http.HttpMethod;

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
		ResponseEntity<String> response = restTemplate.withBasicAuth("miles1", "password123").getForEntity("/jobseeker/21", String.class); 

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		// take JSON of response body and map every field to entries 
		DocumentContext documentContext = JsonPath.parse(response.getBody()); 

		// create test variables 
		LocalDate correct_postDate = LocalDate.of(2025,9,30); 
		LocalDate correct_closeDate = LocalDate.of(2025,12,31); 

		// assert that data returned is expected, testing only unique types, not all
		assertThat(documentContext.read("$.jobID", Long.class)).isEqualTo(21L);
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
		ResponseEntity<String> response = restTemplate.withBasicAuth("miles1", "password123").getForEntity("/jobseeker/0", String.class); 

		// assert that the response is 404 and returns an empty body 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
		assertThat(response.getBody()).isBlank(); 
	}

	/// Request Type : GET
	/// Security : AUTHENTICATION 
	/// Description : GET request with bad credentials, unauthenticated request should return HTTP "401 UNAUTHORIZED" (FAILING TEST)
	@Test
	void getBadCredentials() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("BAD_CRED", "password123").getForEntity("/jobseeker", String.class); 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED); 


		response = restTemplate.withBasicAuth("miles1", "BAD_PASS").getForEntity("/jobseeker", String.class); 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED); 
	}

	/// Request Type : GET 
	/// Description : when requested for list of job entry that exists, should return them 
	@Test 
	void getJobEntryList() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("miles1", "password123").getForEntity("/jobseeker", String.class); 
 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 

		DocumentContext documentContext = JsonPath.parse(response.getBody()); 
		int jobEntryCount = documentContext.read("$.length()"); 
		assertThat(jobEntryCount).isEqualTo(3); 

		JSONArray jobIDs = documentContext.read("$..jobID");
		assertThat(jobIDs).containsExactlyInAnyOrder(20, 21, 22);

		JSONArray jobPays = documentContext.read("$..jobPay"); 
		assertThat(jobPays).containsExactlyInAnyOrder(3000, 4000, 5000); 



	}

	/// Request Type : GET 
	/// Description : when requested for page of existing job entries, return successfully 
	@Test
	void getPageOfJobEntries() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("miles1", "password123").getForEntity("/jobseeker?page=0&size=1", String.class); 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]"); 
		assertThat(page.size()).isEqualTo(1); 
	}

	/// Request Type : GET 
	/// Description : when requested for page of existing job entries with descending order, return successfully
	@Test
	void getSortedPageOfJobEntries() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("miles1", "password123").getForEntity("/jobseeker?page=0&size=1&sort=jobID,desc", String.class); 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]"); 
		assertThat(page.size()).isEqualTo(1); 

		// expected result returns jobID 22, with jobPay 5000 
		int jobPay = documentContext.read("$[0].jobPay"); 
		assertThat(jobPay).isEqualTo(5000);
	}

	/// Request Type : GET 
	/// Description : correct GET request for page of job entries, PARAMETERS MISSING (test for default)
	/// Expect : "200 OK", default sorting should be ascending order of jobID
	@Test
	void getDefaultPageOfJobEntries() {
		ResponseEntity<String> response = restTemplate.withBasicAuth("miles1", "password123").getForEntity("/jobseeker", String.class); 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]"); 
		assertThat(page.size()).isEqualTo(3); 

		// expected result returns jobID 22, with jobPay 5000 
		JSONArray jobPays = documentContext.read("$..jobPay"); 
		assertThat(jobPays).containsExactly(3000,4000,5000);
	}

	/// Request Type : POST 
	/// Description : non-failing POST request to API, database should update 
	/// Expect : "201 CREATED", GET request to new resource location to be "200 OK", with ownership belonging to poster 
	/// NOTE : creates new job entry, needs @DirtiesContext
	@DirtiesContext
	@Test
	void createNewJobEntry() { 
		LocalDate postDateEntry = LocalDate.parse("2025-01-30");
		LocalDate closeDateEntry = LocalDate.parse("2025-05-30");
		JobEntry newEntry = new JobEntry("Marketing Intern", "Meta", postDateEntry, closeDateEntry, "Texas", 4, "Internship", 12000, "https://meta.com", null, "jacob"); 

		ResponseEntity<Void> responsePOST = restTemplate.withBasicAuth("miles1", "password123").postForEntity("/jobseeker", newEntry, Void.class); 

		assertThat(responsePOST.getStatusCode()).isEqualTo(HttpStatus.CREATED); 

		// the response should contain a header with the location of resource created 
		URI jobEntryLocation = responsePOST.getHeaders().getLocation();
		ResponseEntity<String> getResponse = restTemplate.withBasicAuth("miles1", "password123").getForEntity(jobEntryLocation, String.class); 

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK); 

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody()); 
		Number id = documentContext.read("$.jobID"); 
		Integer jobPay = documentContext.read("$.jobPay"); 
		String owner = documentContext.read("$.owner"); 

		assertThat(id).isNotNull(); 
		assertThat(jobPay).isEqualTo(12000);
		assertThat(owner).isEqualTo("miles1");

	}

	/// Request Type : PUT 
	/// Description : non-failing PUT request, specified record should update with new job entry record 
	/// Expect : "204 NO_CONTENT", GET request comparison should contain new value 
	/// NOTE : needs @DirtiesContext, also requires correct owner of jobEntry for permission 
	@DirtiesContext 
	@Test
	void putReplaceJobEntryField() {
		JobEntry jobEntryUpdate = new JobEntry("Machine Testing", "LinkedIn", LocalDate.of(2025, 8, 30), LocalDate.of(2025, 12, 30), "San Diego", 8, "Co-Op", 5000, "https://linkedin.com", null, null); 
		HttpEntity<JobEntry> request = new HttpEntity<>(jobEntryUpdate); 
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("miles1", "password123")
				.exchange("/jobseeker/20", HttpMethod.PUT, request, Void.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);  
		
		ResponseEntity<String> getResponse = restTemplate.withBasicAuth("miles1","password123").getForEntity("/jobseeker/20", String.class); 
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK); 

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody()); 
		Number id = documentContext.read("$.jobID");
		String jobName = documentContext.read("$.jobName"); 
		String jobLocation = documentContext.read("$.jobLocation"); 

		assertThat(id).isEqualTo(20); 
		assertThat(jobLocation).isEqualTo("San Diego"); 
		assertThat(jobName).isEqualTo("Machine Testing"); 
	}

	/// Request Type : PUT 
	/// Description : failing PUT request, unknown record should not replace (suddenly exist) 
	/// Expect : "404 NOT_FOUND"
	@Test
	void putNonExistentJobEntry() {
		JobEntry nonExistentEntry = new JobEntry("Machine Testing", "LinkedIn", LocalDate.of(2025, 8, 30), LocalDate.of(2025, 12, 30), "San Diego", 8, "Co-Op", 5000, "https://linkedin.com", null, null);
		HttpEntity<JobEntry> request= new HttpEntity<>(nonExistentEntry); 
		ResponseEntity<Void> response = restTemplate
			.withBasicAuth("miles1", "password123")
			.exchange("/jobseeker/99999999", HttpMethod.PUT, request, Void.class); 

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
	}

	/// Request Type : PUT 
	/// Description : failing PUT request, unauthorized user should not be able to replace another user's job posting
	/// Expect : "404 NOT_FOUND"
	@Test
	void putUnauthorized() {
		JobEntry jobEntryUpdate = new JobEntry("Machine Testing", "LinkedIn", LocalDate.of(2025, 8, 30), LocalDate.of(2025, 12, 30), "San Diego", 8, "Co-Op", 5000, "https://linkedin.com", null, null); 
		HttpEntity<JobEntry> request = new HttpEntity<>(jobEntryUpdate); 
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("job-searcher", "no-jobs-posted")
				.exchange("/jobseeker/20", HttpMethod.PUT, request, Void.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);  
	}

	/// Request Type : DELETE 
	/// Description : successful DELETE request, jobEntry with associated ID should be deleted when requested by respective owner 
	/// Expect : "204 NO_CONTENT"
	/// Note : needs DirtiesContext
	@DirtiesContext
	@Test
	void deleteAuthorized() {
		// .exchange() used instead of .delete(), .delete() does not return body thus no status code returned
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("miles1", "password123")
				.exchange("/jobseeker/20", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("miles1", "password123")
				.getForEntity("/jobseeker/20", String.class); 

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
	}

	/// Request Type : DELETE 
	/// Description : failing DELETE request, DELETE request by non-owner of job entry 
	/// Expect : "404 NOT_FOUND"	
	@Test
	void deleteUnauthorized() {
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("job-searcher", "no-jobs-posted")
				.exchange("/jobseeker/20", HttpMethod.DELETE, null, Void.class); 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 

		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("miles1", "password123")
				.getForEntity("/jobseeker/20",String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	/// Request Type : DELETE 
	/// Description : failing DELETE request, DELETE non-existent job entry
	/// Expect : "404 NOT_FOUND"
	@Test 
	void deleteNonExistentJobEntry() {
		ResponseEntity<Void> response = restTemplate
				.withBasicAuth("miles1", "password123")
				.exchange("/jobseeker/99999", HttpMethod.DELETE, null, Void.class); 
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
	}

}
