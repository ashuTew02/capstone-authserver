package com.capstone.authServer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A standardized response body for both success and error responses.
 * 
 * @param <T> Type of the data payload if any (e.g., List<FindingResponseDTO>, etc.).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private String status;   // "success" or "error"
    private int statusCode;  // e.g., 200, 400, 500
    private String message;  // e.g., "OK", "Internal Server Error", ...
    private T data;          // This can be any object/DTO (optional)

    public ApiResponse() { }

    public ApiResponse(String status, int statusCode, String message, T data) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        return new ApiResponse<>("success", statusCode, message, data);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>("error", statusCode, message, null);
    }

    // --- Getters and Setters ---
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
