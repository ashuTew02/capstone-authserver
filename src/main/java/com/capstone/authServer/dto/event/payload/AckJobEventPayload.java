package com.capstone.authServer.dto.event.payload;

import com.capstone.authServer.model.JobStatus;

public class AckJobEventPayload {
    private String jobId;
    private JobStatus jobStatus;

    public AckJobEventPayload() {}

    public AckJobEventPayload(String jobId, JobStatus jobStatus) {
        this.jobId = jobId;
        this.jobStatus = jobStatus;
    }
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public JobStatus getJobStatus() {
        return jobStatus;
    }
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

}
