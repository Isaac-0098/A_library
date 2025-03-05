package cn.agehaa.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import cn.agehaa.dao.BookDao;
import cn.agehaa.pojo.BookPojo;

@Service
public class BookServiceImpl {

    @Autowired
    BookDao BookDao;
    
    // 已有的添加图书方法
    public void addBook(int id, String code, String title, String author, String cover, String category, String description, String downloadUrl) {
        BookDao.insert(new BookPojo(id, code, title, author, cover, category, description, downloadUrl));
    }

    // 删除图书
    public void delBook(int id) {
        BookDao.deleteById(id);
    }

    // 更新图书
    public void updateBook(int id, String code, String title, String author, String cover, String category, String description, String downloadUrl) {
        BookPojo book = new BookPojo(id, code, title, author, cover, category, description, downloadUrl);
        BookDao.updateById(book);
    }

    // 根据ID查询图书
    public BookPojo findBookById(int id) {
        return BookDao.selectById(id);
    }

    // 根据书名查询图书
    public List<BookPojo> findBookByname(String title) {
        QueryWrapper<BookPojo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", title);
        return BookDao.selectList(queryWrapper);
    }

    // 查询所有图书
    public List<BookPojo> findAllBooks() {
        return BookDao.selectList(null);
    }

    // 获取所有分类
    public List<String> getAllCategories() {
        // 使用 HashSet 来自动去重
        Set<String> categorySet = new HashSet<>();
        List<BookPojo> books = BookDao.selectList(null);
        
        // 将所有分类添加到 Set 中（自动去重）
        for (BookPojo book : books) {
            if (book.getCategory() != null && !book.getCategory().trim().isEmpty()) {
                categorySet.add(book.getCategory().trim());
            }
        }
        
        // 转换回 List 并返回
        return new ArrayList<>(categorySet);
    }
}
