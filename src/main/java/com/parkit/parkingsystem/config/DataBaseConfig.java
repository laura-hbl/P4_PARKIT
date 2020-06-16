package com.parkit.parkingsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Creates a connection to database.
 *
 * @author Laura
 */
public class DataBaseConfig {

  /**
   * DataBaseConfig logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("DataBaseConfig");

  /**
   * Establishes a connection to MySQL database.
   *
   * @return a Connection instance
   */
  public Connection getConnection() throws ClassNotFoundException,
      SQLException {
    LOGGER.info("Create DB connection");
    Class.forName("com.mysql.cj.jdbc.Driver");

    Properties info = new Properties();
    info.put("user", "root");
    info.put("password", "rootroot");

    return DriverManager.getConnection("jdbc:mysql://localhost:3306/prod?"
    + "serverTimezone=Europe/Paris", info);
  }
}
