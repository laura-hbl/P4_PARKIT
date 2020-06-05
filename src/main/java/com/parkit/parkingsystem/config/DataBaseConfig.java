package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConfig {

    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        logger.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");

        Properties info = new Properties();
        info.put("user", "root");
        info.put("password", "rootroot");

        return DriverManager.getConnection("jdbc:mysql://localhost:3306/prod?serverTimezone=Europe/Paris", info);
    }
}