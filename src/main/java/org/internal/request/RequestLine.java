package org.internal.request;

public class RequestLine {
    private String method;
    private String requestTarget;
    private String httpVersion;

	// constructor, getters, setters
	public RequestLine(String method, String requestTarget, String httpVersion) {
		this.method = method;
		this.requestTarget = requestTarget;
		this.httpVersion = httpVersion;
	} 
    
	public String getMethod() {
		return method;
	}

	public String getRequestTarget() {
		return requestTarget;
	}
	
	public String getHttpVersion() {
		return httpVersion;
	}
}

