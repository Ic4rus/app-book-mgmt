package com.icarus.demo.book_mgmt.controller;

import com.icarus.demo.book_mgmt.entity.Book;
import com.icarus.demo.book_mgmt.service.ElasticsearchService;
import com.icarus.demo.book_mgmt.utils.FileUtils;
import com.icarus.demo.book_mgmt.utils.PdfUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @PostMapping("/upload")
    public ResponseEntity uploadBook(
            @RequestParam MultipartFile file)
            throws IOException {

        if (!file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
            return ResponseEntity.badRequest().body(error("Only PDF files are allowed"));
        }

        String filePath = FileUtils.saveBook(file);
        String thumbnailPath = PdfUtils.generateThumbnail(file);

        Book book = PdfUtils.loadBook(file);
        book.setFilePath(filePath);
        book.setThumbnailPath(thumbnailPath);

        elasticsearchService.indexBook(book);

        book.setContent(null);
        return ResponseEntity.ok(success(book));
    }

    @GetMapping
    public ResponseEntity getAllBooks(
            @RequestParam(required = false) String keyword) {

        List<Book> books = elasticsearchService.listBook(keyword);

        return ResponseEntity.ok(success(books));
    }

    @GetMapping("/download")
    public void download(
            HttpServletResponse response,
            @RequestParam String filePath,
            @RequestParam(defaultValue = "true") boolean isBook) throws IOException {

        filePath = FileUtils.generatePath(FileUtils.getFolder(isBook), filePath);

        File file = new File(filePath);
        if (file.exists()) {
            OutputStream os = response.getOutputStream();
            Files.copy(file.toPath(), os);
            os.flush();
        }
    }

    @Data
    public static class BodyEntity<T> {

        private Integer code;

        private String message;

        private T data;
    }

    public static <T> BodyEntity success(T data) {

        BodyEntity body = new BodyEntity<>();
        body.setCode(0);
        body.setMessage("Success");
        body.setData(data);

        return body;
    }

    public static BodyEntity error(String message) {

        BodyEntity body = new BodyEntity<>();
        body.setCode(1);
        body.setMessage(message);

        return body;
    }
}
