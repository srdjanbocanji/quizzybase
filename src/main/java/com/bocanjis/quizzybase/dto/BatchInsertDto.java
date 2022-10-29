package com.bocanjis.quizzybase.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchInsertDto {

    private List<QuestionDto> items;
}
