package com.green.greengramver.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
//생성자를 넣고싶으면 @Setter를 사용할 수 없다.
//Setter를 빼서 지금 immutable한 상태
public class Paging {
    private final static int DEFAULT_PAGE_SIZE = 20; //magic number
    @Schema(example = "1", description = "Selected Page")
    private int page;
    @Schema(example = "30", description = "item count per page")
    private int size;
    @JsonIgnore
    private int startIdx;

    public Paging(Integer page, Integer size) {
        this.page = (page == null || page <= 0) ? 1 : page;
        this.size = (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : size;
        this.startIdx = (this.page - 1) * this.size;
    }
}
