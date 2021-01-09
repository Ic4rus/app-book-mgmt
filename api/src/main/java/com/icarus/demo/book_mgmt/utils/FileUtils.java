package com.icarus.demo.book_mgmt.utils;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

import static org.apache.commons.io.FileUtils.*;

public class FileUtils {

    public static final String STORAGE = "storage";

    public static final String BOOK = "books";

    public static final String THUMBNAIL = "thumbnails";

    public static String generatePath(String... subPaths) {
        return String.join(File.separator, subPaths);
    }

    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    public static String generateFilePath(String fileName) {
        return generatePath(STORAGE, BOOK, fileName);
    }

    public static String generateThumbnailPath(String fileName) {
        return generatePath(STORAGE, THUMBNAIL, fileName);
    }

    public static String getFolder(boolean isBook) {
        return generatePath(STORAGE, isBook ? BOOK : THUMBNAIL);
    }

    public static String saveBook(MultipartFile file) throws IOException {

        String fileName = randomString();

        String filePath = generateFilePath(fileName);

        file.transferTo(Paths.get(filePath));

        return fileName;
    }

    public static void cleanStorage() throws IOException {

        cleanDirectory(new File(generatePath(STORAGE, BOOK)));

        cleanDirectory(new File(generatePath(STORAGE, THUMBNAIL)));
    }

}
