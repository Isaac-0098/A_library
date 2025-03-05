package cn.agehaa;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.agehaa.service.impl.BookServiceImpl;

@SpringBootTest
@MapperScan("cn.agehaa.dao")
class BackendApplicationTests {

	@Autowired
	BookServiceImpl bookService;

	@Test
	void contextLoads() {

	}
}
