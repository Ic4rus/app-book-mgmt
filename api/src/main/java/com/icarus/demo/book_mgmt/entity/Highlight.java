package com.icarus.demo.book_mgmt.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;

@Data
@NoArgsConstructor
public class Highlight {

    private Integer pageNumber;

    private String text;

    public Highlight(Integer pageNumber, String text) {
        this.pageNumber = pageNumber;

        this.text = text.replaceAll("\\n", "<br />");
    }
}
