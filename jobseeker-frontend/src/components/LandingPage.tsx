'use client'

import { useState } from 'react'; 
import { SearchFilters } from '@/types'; 

interface LandingPageProps {
    onSearch : (filters: SearchFilters) => void; 
}

export default function LandingPage ({onSearch} : LandingPageProps) {
    const [keyword, setKeyword] = useState(''); 
    const [location, setLocation] = useState(''); 

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSearch({
            keyword,
            location,
            jobType:''
        });
    }

    return (
        <section>
            <form onSubmit={handleSubmit}>
                <input
                    name="keyword"
                    type="text"
                    placeholder="keyword"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)} 
                />

                <input 
                    name="location"
                    type="text"
                    placeholder="location"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                />

                <button type="submit">Search</button>
            </form>
        </section>
    )
}