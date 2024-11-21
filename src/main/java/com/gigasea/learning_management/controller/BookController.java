package com.gigasea.learning_management.controller;

import com.gigasea.learning_management.model.Book;
import com.gigasea.learning_management.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    // Directory to store uploaded books
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploaded_books/";

    public BookController() {
        // Create the directory if it doesn't exist
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    // **Upload Book Endpoint**
    @PostMapping("/upload-book")
    public String uploadBook(@RequestParam("instructorName") String instructorName,
                             @RequestParam("bookTitle") String bookTitle,
                             @RequestParam("bookFile") MultipartFile bookFile,
                             Model model) {
        try {
            // Save file to the system
            String filePath = UPLOAD_DIR + bookFile.getOriginalFilename();
            bookFile.transferTo(new File(filePath));

            // Save book details in the database
            Book book = new Book();
            book.setInstructorName(instructorName);
            book.setTitle(bookTitle);
            book.setPath(filePath);
            bookService.saveBook(book);

            model.addAttribute("message", "Book uploaded successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to upload book: " + e.getMessage());
        }

        return "uploadResult"; // Redirect to a success/failure page
    }

    // **View Books Endpoint**
    @GetMapping("/books")
    public String viewBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "viewBooks"; // HTML page for listing books
    }
    @GetMapping("/upload-book")
    public String showUploadForm() {
        return "uploadBook"; // Renders the uploadBook.html page
    }

    // **Open Book Endpoint**
    @GetMapping("/open-book/{id}")
    public ResponseEntity<Resource> openBook(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = Paths.get(book.getPath()).toAbsolutePath();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                        .contentType(MediaType.APPLICATION_PDF) // Assuming the books are PDFs
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
