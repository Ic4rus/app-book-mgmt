package com.icarus.demo.book_mgmt.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TotalDTO implements Serializable {

    private Integer value;

    private String relation;
}
