import { JobEntry, SearchFilters } from "@/types"; 
import { mockJobs } from "@/lib/jobs";  

interface JobCardsProps {
    filters: SearchFilters; 
    jobs: JobEntry[]; 
}

export default function JobCards({filters, jobs}: JobCardsProps) {

    const jobList = jobs.filter((job) => job.jobName.toLowerCase().includes(filters.keyword.toLowerCase()) 
                                        && job.jobLocation.toLowerCase().includes(filters.location.toLowerCase())
                                        && job.jobType.toLowerCase().includes(filters.jobType.toLowerCase()));

    return (
        <div className="max-w-4xl mx-auto p-6">
            <h2 className="text-2xl font-bold mb-6">Available Jobs</h2>
            <div className="space-y-4">
                {jobList.map((job) => (
                    <div key={job.jobID} className="bg-white border border-gray-300 rounded-xl p-6 shadow-sm">
                        <div className="flex justify-between items-start">
                            <div className="flex-1">
                                {/* Header Section */}
                                <div className="mb-4">
                                    <h3 className="text-xl font-semibold text-gray-900 mb-1">{job.jobName}</h3>
                                    <p className="text-gray-600 mb-1">{job.companyName}</p>
                                    <p className="text-sm text-gray-500 mb-3">{job.jobLocation}</p>
                                    <span className="inline-block bg-blue-50 text-blue-700 text-sm px-3 py-1 rounded-full border border-blue-200">
                                        {job.jobType}
                                    </span>
                                </div>

                                {/* Job Description */}
                                <div className="mb-4">
                                    <p className="text-gray-700 leading-relaxed">
                                        Lorem ipsum dolor sit amet consectetur, adipisicing elit. Porro, assumenda iste. Ab, 
                                        ipsum iste quae totam ex quibusdam! Sequi, voluptatibus hic. Ut reprehenderit sint...
                                    </p>
                                </div>

                                {/* Job Pay and Duration */}
                                <div className="mb-4 space-y-1">
                                    <p className="text-gray-800"><span className="font-medium">Job Pay:</span> $ {job.jobPay.toLocaleString()} per month</p>
                                    <p className="text-gray-800"><span className="font-medium">Duration:</span> {job.jobDuration} months</p>
                                </div>

                                {/* Posted Date + Calendar Icon */}
                                <div className="flex items-center text-sm text-gray-600">
                                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                    <span>Posted: {job.postDate}</span>
                                </div>
                            </div>
                            
                            {/* Company Logo */}
                            <div className="ml-6">
                                <div className="w-12 h-12 bg-gray-100 border border-gray-200 rounded flex items-center justify-center">
                                    <span className="text-xs text-gray-400">Logo</span>
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}