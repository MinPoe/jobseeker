'use client';

import LandingPage from "@/components/LandingPage";
import JobCards from "@/components/JobCards"; 

import { useState } from 'react'; 
import { SearchFilters } from '@/types'

export default function Home() {
  const [searchFilters, setSearchFilters] = useState<SearchFilters>({
    keyword:'',
    location:'',
    jobType:''
  }); 
  
  const handleSearch = (filters: SearchFilters) => {
    setSearchFilters(filters); 
    console.log("Searching for: ", filters); 
  }; 

  return (
    <main>
      <LandingPage onSearch={handleSearch} />
      <JobCards filters={searchFilters}/>
    </main>
  ); 
};