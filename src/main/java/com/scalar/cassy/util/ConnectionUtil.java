package com.scalar.cassy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionUtil {
  private static final Logger logger = LoggerFactory.getLogger(ConnectionUtil.class);

  public static Connection create(String dbUrl) throws SQLException {
    Connection connection = DriverManager.getConnection(dbUrl);
    connection.setAutoCommit(true);
    return connection;
  }

  public static void close(Connection connection) {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      logger.warn("failed to close the connection", e);
    }
  }
}
