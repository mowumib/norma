package com.hotelbooking.norma.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ResponseModel {
    public LocalDateTime timeCreated = LocalDateTime.now();
    public int statusCode;
    public String message;
    public Object data;

    public int pageNo;
    public int pageSize;
    public Integer prevPage;
    public Integer nextPage;
    public long totalElements;
    public int totalPages;
    public boolean hasPrevious;
    public boolean hasNext;
    public boolean first;
    public boolean last;
    public boolean empty;


    public ResponseModel(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
    
    public ResponseModel(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    public ResponseModel(int statusCode, String message, Object data, int pageNo, int pageSize, Integer prevPage, Integer nextPage, long totalElements, int totalPages, boolean hasPrevious, boolean hasNext, boolean first, boolean last, boolean empty) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;

        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.prevPage = prevPage;
        this.nextPage = nextPage;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.first = first;
        this.last = last;
        this.empty = empty;
    }

    public ResponseModel(int statusCode, String message, Object data, int pageSize, int pageNo, long totalElements, int totalPages, boolean last, boolean first) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.first = first;
    }

}
