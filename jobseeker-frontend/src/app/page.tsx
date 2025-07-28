'use client';

import LandingPage from "@/components/LandingPage";
import JobCards from "@/components/JobCards"; 

import { useState } from 'react'; 
import { SearchFilters, JobEntry } from '@/types'

export default function Home() {
  const [searchFilters, setSearchFilters] = useState<SearchFilters>({
    keyword:'',
    location:'',
    jobType:''
  }); 

  const [jobs, setJobs] = useState<JobEntry[]>([]);

  const [loading, setLoading] = useState(false); 

  const [error, setError] = useState<string | null>(null);
  
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

  return (
    <main>
      <LandingPage onSearch={handleSearch} />
      <JobCards filters={searchFilters} jobs={jobs} />
    </main>
  ); 
};
