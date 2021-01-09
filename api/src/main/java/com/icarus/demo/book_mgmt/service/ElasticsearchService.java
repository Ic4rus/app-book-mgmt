package com.icarus.demo.book_mgmt.service;

import com.icarus.demo.book_mgmt.entity.Book;
import com.icarus.demo.book_mgmt.entity.Highlight;
import com.icarus.demo.book_mgmt.entity.HitDTO;
import com.icarus.demo.book_mgmt.entity.SearchDTO;
import com.icarus.demo.book_mgmt.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ElasticsearchService {

    private static final String ROOT_URI = "http://localhost:9200";

    private static final String INDEX_NAME = "/book";

    @PostConstruct
    private void init() {
        try {
            // Delete file
            FileUtils.cleanStorage();

            // Delete index
            if (checkIndexExists()) {
                deleteIndex();
            }

            // Create index
            createIndex();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkIndexExists() {

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity responseEntity = restTemplate
                    .exchange(ROOT_URI + INDEX_NAME, HttpMethod.HEAD, null, String.class);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void deleteIndex() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(ROOT_URI + INDEX_NAME);
    }

    public void createIndex() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(ROOT_URI + INDEX_NAME, null);
    }

    public void indexBook(Book book) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate
                .postForEntity(ROOT_URI + INDEX_NAME + "/_doc", book, String.class);
        log.info(responseEntity.getStatusCodeValue() + "");
        log.info(responseEntity.getBody());
    }

    public List<Book> listBook(String keyword) {
        RestTemplate restTemplate = new RestTemplate();

        String query;
        if (keyword == null || keyword.length() == 0) {
            query = "{\"query\":{\"match_all\": {}},\"_source\": [\"title\", \"author\", \"numberOfPages\", \"thumbnailPath\", \"filePath\"]}";
        } else {
            query = String.format(
                    "{\"query\": {\"multi_match\": {\"query\": \"%s\", \"fields\": [\"content.page_*\"]}}, " +
                            "\"_source\": [ \"title\", \"author\", \"numberOfPages\", \"filePath\" ], " +
                            "\"highlight\": {\"fields\": {\"content.page_*\": {}}}}", keyword);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(query, headers);

        ResponseEntity<SearchDTO<Book>> responseEntity = restTemplate
                .exchange(
                        ROOT_URI + INDEX_NAME + "/_search",
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<SearchDTO<Book>>(){});

        SearchDTO<Book> result = responseEntity.getBody();

        List<HitDTO<Book>> hits = result.getHits().getHits();

        List<Book> books = new ArrayList<>();
        hits.forEach(hit -> {
            Book book = hit.getSource();
            Map<String, List<String>> highlight = hit.getHighlight();
            if (highlight != null && !highlight.isEmpty()) {
                highlight.forEach((key, value) -> {
                    int pageNumber = Integer.parseInt(key.split("_")[1]);
                    books.addAll(
                            value.stream()
                                    .map(text -> new Highlight(pageNumber, text))
                                    .map(h -> new Book(book, h))
                                    .collect(Collectors.toList()));
                });
            } else {
                books.add(book);
            }
        });

        return books;
    }

}
