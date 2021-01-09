package com.icarus.demo.book_mgmt.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book implements Serializable {

    private String title;

    private String author;

    private Integer numberOfPages;

    private String thumbnailPath;

    private String filePath;

    private HashMap<String, String> content;

    private Highlight highlight;

    public Book(Book book, Highlight highlight) {
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.numberOfPages = book.getNumberOfPages();
        this.thumbnailPath = book.getThumbnailPath();
        this.filePath = book.getFilePath();
        this.content = book.getContent();
        this.highlight = highlight;
    }
}
