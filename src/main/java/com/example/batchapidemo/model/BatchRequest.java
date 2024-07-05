package com.example.batchapidemo.model;

import java.util.List;

public class BatchRequest {
    private List<BatchOperation> requests;

    public List<BatchOperation> getRequests() {
        return requests;
    }

    public void setRequests(List<BatchOperation> requests) {
        this.requests = requests;
    }
}
