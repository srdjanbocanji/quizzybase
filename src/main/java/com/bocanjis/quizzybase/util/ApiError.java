package com.bocanjis.quizzybase.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiError {

    private int code;
    private String description;
}
