package com.icarus.demo.book_mgmt.utils;

import com.icarus.demo.book_mgmt.entity.Book;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PdfUtils {

    public static Book loadBook(MultipartFile file)
            throws IOException {

        Book book = new Book();
        PdfReader pdfReader = new PdfReader(file.getBytes());
        HashMap<String, String> info = pdfReader.getInfo();

        info.forEach((k, v) -> {
            switch (k) {
                case "Title":
                    book.setTitle(v);
                    break;
                case "Author":
                    book.setAuthor(v);
                    break;
            }
        });

        int numberOfPages = pdfReader.getNumberOfPages();
        book.setNumberOfPages(numberOfPages);

        HashMap<String, String> content = new HashMap<>();
        for (int i = 1; i <= numberOfPages; i++) {
            String text = PdfTextExtractor.getTextFromPage(pdfReader, i);
            content.put("page_" + i, text);
        }

        book.setContent(content);

        return book;
    }

    public static String generateThumbnail(MultipartFile file) throws IOException {

        PDDocument pd = PDDocument.load(file.getBytes());

        PDFRenderer pr = new PDFRenderer(pd);

        BufferedImage bi = pr.renderImageWithDPI(0, 50);

        String fileName = FileUtils.randomString();
        String thumbnailPath = FileUtils.generateThumbnailPath(fileName);

        ImageIO.write(bi, "JPEG", new File(thumbnailPath));

        return fileName;
    }

}
