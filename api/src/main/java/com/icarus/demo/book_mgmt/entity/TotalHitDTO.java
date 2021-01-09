package com.icarus.demo.book_mgmt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TotalHitDTO<T> implements Serializable {

    private TotalDTO total;

    @JsonProperty("max_score")
    private Double maxScore;

    private List<HitDTO<T>> hits;
}
