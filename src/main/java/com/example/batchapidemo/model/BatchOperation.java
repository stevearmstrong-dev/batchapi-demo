package com.example.batchapidemo.model;

import java.util.List;
import java.util.Map;

public class BatchOperation {
    private String method;
    private String url;
    private List<Map<String, Object>> body;

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public List<Map<String, Object>> getBody() { return body; }
    public void setBody(List<Map<String, Object>> body) { this.body = body; }
}
