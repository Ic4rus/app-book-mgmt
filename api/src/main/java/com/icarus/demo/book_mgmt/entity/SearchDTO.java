package com.icarus.demo.book_mgmt.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchDTO<T> implements Serializable {

    private TotalHitDTO<T> hits;

}
