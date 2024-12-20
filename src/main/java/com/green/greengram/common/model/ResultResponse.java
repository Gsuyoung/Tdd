package com.green.greengram.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ResultResponse<T> {
    @Schema(title = "결과 메시지")
private String resultMsg;
    @Schema(title = "결과 내용")
private T resultData;
}
