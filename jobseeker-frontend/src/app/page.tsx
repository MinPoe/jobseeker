'use client';

import LandingPage from "@/components/LandingPage";
import JobCards from "@/components/JobCards"; 
import LoginPage from "@/components/LoginPage"; 

import { useState } from 'react'; 
import { SearchFilters, JobEntry, LoginCredentials } from '@/types'

export default function Home() {
  const [searchFilters, setSearchFilters] = useState<SearchFilters>({
    keyword:'',
    location:'',
    jobType:''
  }); 

  const [jobs, setJobs] = useState<JobEntry[]>([]);

  const [loading, setLoading] = useState(false); 

  const [error, setError] = useState<string | null>(null);

  const [isLoggedIn, setIsLoggedIn] = useState(false); 
  
  const handleSearch = async (filters: SearchFilters) => {
    setSearchFilters(filters); 
    console.log("Searching for: ", filters); 

    setLoading(true); 
    setError(null); 
    
    try {
      const response = await fetch('http://localhost:8080/api', {
        method: 'GET',
        headers: {
          'Authorization': `Basic ${btoa('miles1:password123')}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error("Failed to fetch jobs"); 
      }

      const jobsData = await response.json(); 

      setJobs(jobsData); 
    }

    catch (err) {
      setError('Error loading jobs. Please try again.');
      console.error('Fetch error:', err);
    }

    finally {
      setLoading(false); 
    }
  }; 

  const handleLogin = async (loginCred : LoginCredentials) => {
    setLoading(true);
    setError(null); 

    try {
      const response = await fetch('http://localhost:8080/api', {
        // api fetch 
         
      });

      if (!response.ok) {
        throw new Error("Login Credentials Failed"); 
      }

      const responseData = await response.json();
      const token = responseData.token; 
      document.cookie = "token=" + token; 
      
      setIsLoggedIn(true);
    }

    catch (err) {
      setError('Error logging in. Please try again.');
      console.error('Fetch error:', err);
    }

    finally {
      setLoading(false); 
    }
  };

  return (
    <main>
      {isLoggedIn ? (
        <>
          <LandingPage onSearch={handleSearch} />
          <JobCards filters={searchFilters} jobs={jobs} />
        </>
      ) : (
          <LoginPage onLogin={handleLogin} /> 
      )}
    </main>
  ); 
};
