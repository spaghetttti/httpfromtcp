package org.internal.request;

public class RequestLine {
	private METHOD_NAMES method;
	private String requestTarget;
	private String httpVersion;

	// constructor, getters, setters
	public RequestLine(String method, String requestTarget, String httpVersion) throws Exception {
		if (requestTarget.isBlank()) {
			throw new Exception("request target can't be empty");
		}
		if (httpVersion.isBlank()) {
			throw new Exception("httpVersion target can't be empty");
		} else if (!httpVersion.equals("1.1")) {
			throw new Exception("unsupported httpVersion");
		}
		try {
			this.method = METHOD_NAMES.valueOf(method);
		} catch (Exception e) {
			throw new Exception("invalid method name");
		}
		this.requestTarget = requestTarget;
		this.httpVersion = httpVersion;
	}

	public String getMethod() {
		return method.toString();
	}

	public String getRequestTarget() {
		return requestTarget;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public String toString() {
		return new StringBuilder().append("\n").append("- Method: ").append(getMethod()).append("\n").append("- Target:")
				.append(getRequestTarget()).append("\n").append("- Version: ").append(getHttpVersion()).append("\n").toString();
	}

}

enum METHOD_NAMES {
	GET,
	POST,
	PUT,
	DELETE,
	PATCH
}
