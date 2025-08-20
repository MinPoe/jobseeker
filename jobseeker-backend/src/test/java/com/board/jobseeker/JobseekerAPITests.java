package com.board.jobseeker;

import com.board.jobseeker.auth.AuthDTO;
import com.board.jobseeker.jobs.JobEntry;
import com.board.jobseeker.util.AuthHelpers;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class JobseekerAPITests {
    
    // dependency injection (autowired) for test helper to aid in HTTP request creation 
    @Autowired 
    private TestRestTemplate restTemplate; 
    
    @Autowired
    private AuthHelpers authHelper;
    
    private String validToken;

    @BeforeEach
    void setUp() {
        restTemplate.getRestTemplate().setRequestFactory(
            new HttpComponentsClientHttpRequestFactory()
        );
        
        validToken = authHelper.getAuthToken(restTemplate, "miles1", "password123");
    }

	/// Preliminary Test : Ensure that logging in works 
	@Test
	void testLogin() {
		AuthDTO.LoginRequest loginRequest = new AuthDTO.LoginRequest();
		loginRequest.setUsername("miles1");
		loginRequest.setPassword("password123");
		
		ResponseEntity<AuthDTO.AuthResponse> response = restTemplate.postForEntity(
			"/api/auth/login", 
			loginRequest, 
			AuthDTO.AuthResponse.class
		);
		
		System.out.println("Login Status: " + response.getStatusCode());
		System.out.println("Response Body: " + response.getBody());
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
    /// Request Type : GET 
    /// Description : given an existing job entry, should be able to request 'get' the entry (PASSING test)
    @Test
    void getAvailableJobEntry() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/21", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );

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
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/0", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );

        // assert that the response is 404 and returns an empty body 
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
        assertThat(response.getBody()).isBlank(); 
    }

    /// Request Type : GET
    /// Security : AUTHENTICATION 
    /// Description : GET request with bad credentials, unauthenticated request should return HTTP "401 UNAUTHORIZED" (FAILING TEST)
    @Test
    void getBadCredentials() {
        // Test with no authorization header
        ResponseEntity<String> response = restTemplate.getForEntity("/api", String.class); 
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED); 

        // Test with invalid token
        response = restTemplate.exchange(
            "/api", 
            HttpMethod.GET, 
            authHelper.createAuthEntity("invalid-token"), 
            String.class
        ); 
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED); 
    }

    /// Request Type : GET 
    /// Description : when requested for list of job entry that exists, should return them 
    @Test 
    void getJobEntryList() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );
 
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 

        DocumentContext documentContext = JsonPath.parse(response.getBody()); 
        int jobEntryCount = documentContext.read("$.length()"); 
        assertThat(jobEntryCount).isEqualTo(3); 

        JSONArray jobIDs = documentContext.read("$..jobID");
        assertThat(jobIDs).containsExactlyInAnyOrder(20, 21, 22);

        // expected result returns jobID 22, with jobPay 5000 
        JSONArray jobPays = documentContext.read("$..jobPay"); 
        assertThat(jobPays).containsExactlyInAnyOrder(3000, 4000, 5000); 
    }

    /// Request Type : GET 
    /// Description : when requested for page of existing job entries, return successfully 
    @Test
    void getPageOfJobEntries() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api?page=0&size=1", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]"); 
        assertThat(page.size()).isEqualTo(1); 
    }

    /// Request Type : GET 
    /// Description : when requested for page of existing job entries with descending order, return successfully
    @Test
    void getSortedPageOfJobEntries() {
        ResponseEntity<String> response = restTemplate.exchange(
            "/api?page=0&size=1&sort=jobID,desc", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );
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
        ResponseEntity<String> response = restTemplate.exchange(
            "/api", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK); 

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]"); 
        assertThat(page.size()).isEqualTo(3); 

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

        ResponseEntity<Void> responsePOST = restTemplate.exchange(
            "/api", 
            HttpMethod.POST, 
            authHelper.createAuthEntity(newEntry, validToken), 
            Void.class
        );

        assertThat(responsePOST.getStatusCode()).isEqualTo(HttpStatus.CREATED); 

        // the response should contain a header with the location of resource created 
        URI jobEntryLocation = responsePOST.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.exchange(
            jobEntryLocation.toString(), 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );

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
        
        // .exchange() used instead of .delete(), .delete() does not return body thus no status code returned
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/20", 
            HttpMethod.PUT, 
            authHelper.createAuthEntity(jobEntryUpdate, validToken), 
            Void.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);  
        
        ResponseEntity<String> getResponse = restTemplate.exchange(
            "/api/20", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );
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
        
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/99999999", 
            HttpMethod.PUT, 
            authHelper.createAuthEntity(nonExistentEntry, validToken), 
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
    }

    /// Request Type : PUT 
    /// Description : failing PUT request, unauthorized user should not be able to replace another user's job posting
    /// Expect : "404 NOT_FOUND"
    @Test
    void putUnauthorized() {
        String unauthorizedToken = authHelper.getAuthToken(restTemplate, "job-searcher", "no-jobs-posted");
        JobEntry jobEntryUpdate = new JobEntry("Machine Testing", "LinkedIn", LocalDate.of(2025, 8, 30), LocalDate.of(2025, 12, 30), "San Diego", 8, "Co-Op", 5000, "https://linkedin.com", null, null); 
        
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/20", 
            HttpMethod.PUT, 
            authHelper.createAuthEntity(jobEntryUpdate, unauthorizedToken), 
            Void.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);  
    }

    /// Request Type : PATCH 
    /// Description : non-failing PATCH request, record should update with new patch
    /// Expect : "204 NO_CONTENT", GET request comparison should contain new value
    /// Note : needs DirtiesContext
    @DirtiesContext
    @Test
    void patchAuthorized() {
        String patchBody = "[{\"op\": \"replace\", \"path\": \"/jobName\", \"value\": \"Updated Job Name\"}]";
        
        HttpHeaders headers = authHelper.createAuthHeadersWithContentType(validToken, MediaType.valueOf("application/json-patch+json"));
        HttpEntity<String> requestEntity = new HttpEntity<>(patchBody, headers);
    
        ResponseEntity<Void> patchResponse = restTemplate.exchange(
            "/api/20", 
            HttpMethod.PATCH, 
            requestEntity, 
            Void.class
        );
        
        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT); 
        
        ResponseEntity<JobEntry> getResponse = restTemplate.exchange(
            "/api/20", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            JobEntry.class
        );
        
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getJobName()).isEqualTo("Updated Job Name"); 
    }

    /// Request Type : DELETE 
    /// Description : successful DELETE request, jobEntry with associated ID should be deleted when requested by respective owner 
    /// Expect : "204 NO_CONTENT"
    /// Note : needs DirtiesContext
    @DirtiesContext
    @Test
    void deleteAuthorized() {
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/20", 
            HttpMethod.DELETE, 
            authHelper.createAuthEntity(validToken), 
            Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.exchange(
            "/api/20", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
    }

    /// Request Type : DELETE 
    /// Description : failing DELETE request, DELETE request by non-owner of job entry 
    /// Expect : "404 NOT_FOUND"	
    @Test
    void deleteUnauthorized() {
        String unauthorizedToken = authHelper.getAuthToken(restTemplate, "job-searcher", "no-jobs-posted");
        
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/20", 
            HttpMethod.DELETE, 
            authHelper.createAuthEntity(unauthorizedToken), 
            Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 

        ResponseEntity<String> getResponse = restTemplate.exchange(
            "/api/20", 
            HttpMethod.GET, 
            authHelper.createAuthEntity(validToken), 
            String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /// Request Type : DELETE 
    /// Description : failing DELETE request, DELETE non-existent job entry
    /// Expect : "404 NOT_FOUND"
    @Test 
    void deleteNonExistentJobEntry() {
        ResponseEntity<Void> response = restTemplate.exchange(
            "/api/99999", 
            HttpMethod.DELETE, 
            authHelper.createAuthEntity(validToken), 
            Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND); 
    }
}