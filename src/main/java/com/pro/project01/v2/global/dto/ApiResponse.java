package com.pro.project01.v2.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {
    private String status;  // success / fail
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.of("success", data);
    }

    public static <T> ApiResponse<T> fail(T data) {
        return ApiResponse.of("fail", data);
    }
}
