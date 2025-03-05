package cn.agehaa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.agehaa.pojo.BookPojo;
import cn.agehaa.service.impl.BookServiceImpl;
import cn.agehaa.utils.Result;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookServiceImpl bookService;

    // 添加图书
    @PostMapping("/add")
    public String addBook(@RequestParam("id") Integer id,
                          @RequestParam("code") String code,
                          @RequestParam("title") String title,
                          @RequestParam("author") String author,
                          @RequestParam("cover") String cover,
                          @RequestParam("category") String category,
                          @RequestParam("description") String description,
                          @RequestParam(value = "downloadUrl", required = false) String downloadUrl) {
        bookService.addBook(id, code, title, author, cover, category, description, downloadUrl);
        return Result.okGetString("添加成功");
    }

    // 删除图书
    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Integer id) {
        bookService.delBook(id);
        return Result.okGetString("删除成功");
    }

    // 更新图书
    @PutMapping("/update")
    public String updateBook(@RequestParam("id") Integer id,
                             @RequestParam("code") String code,
                             @RequestParam("title") String title,
                             @RequestParam("author") String author,
                             @RequestParam("cover") String cover,
                             @RequestParam("category") String category,
                             @RequestParam("description") String description,
                             @RequestParam(value = "downloadUrl", required = false) String downloadUrl) {
        bookService.updateBook(id, code, title, author, cover, category, description, downloadUrl);
        return Result.okGetString("更新成功");
    }

    // 根据ID查询图书
    @GetMapping("/get/{id}")
    public BookPojo getBookById(@PathVariable("id") Integer id) {
        return bookService.findBookById(id);
    }

    // 根据书名查询图书
    @GetMapping("/search")
    public List<BookPojo> searchBookByName(@RequestParam("title") String title) {
        return bookService.findBookByname(title);
    }

    // 查询所有图书
    @GetMapping("/list")
    public String getAllBooks() {
        try {
            List<BookPojo> books = bookService.findAllBooks();
            System.out.println("Found books: " + books.size());
            return Result.okGetList("获取图书列表成功", books);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting books: " + e.getMessage());
            return Result.errorGetString("获取图书列表失败: " + e.getMessage());
        }
    }

    // 获取所有分类
    @GetMapping("/categories")
    public String getCategories() {
        List<String> categories = bookService.getAllCategories();
        String result = Result.okGetList("获取分类成功", categories);
        System.out.println("Categories response: " + result);
        return result;
    }
}
