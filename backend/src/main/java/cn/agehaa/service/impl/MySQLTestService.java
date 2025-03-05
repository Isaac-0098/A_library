package cn.agehaa.service.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class MySQLTestService {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void testConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            if (connection != null) {
                System.out.println("MySQL connection established successfully.");
            }
        }
    }
}

