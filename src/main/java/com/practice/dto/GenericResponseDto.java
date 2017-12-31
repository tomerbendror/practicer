package com.practice.dto;

/**
 * User: tomer
 */
public class GenericResponseDto <T>{
    private T responseObject;
    private String errorMessage;

    public GenericResponseDto() {
    }

    private GenericResponseDto(T responseObject) {
        this.responseObject = responseObject;
    }

    private GenericResponseDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private GenericResponseDto(T responseObject, String errorMessage) {
        this.responseObject = responseObject;
        this.errorMessage = errorMessage;
    }

    public static <T> GenericResponseDto<T> success(T obj) {
        return new GenericResponseDto<T>(obj);
    }

    public static <T> GenericResponseDto<T> failure(String errorMessage) {
        return new GenericResponseDto<T>(errorMessage);
    }

    public static <T> GenericResponseDto<T> failure(T obj, String errorMessage) {
        return new GenericResponseDto<T>(obj, errorMessage);
    }

    public T getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(T responseObject) {
        this.responseObject = responseObject;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isError(){
        return !(errorMessage == null || "".equals(errorMessage));
    }
}