package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.config.DataBaseConfig;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class DataBaseConfigTest {

  private DataBaseConfig dataBaseConfig;

  @BeforeEach
  public void setUp() {
    dataBaseConfig = new DataBaseConfig();
  }

  @Test
  @DisplayName("Given a connection was declared, when getConnection, then a Connexion should be established")
  public void givenDataBaseConnection_whenGetConnection_thenConnectionShouldBeEstablished()
  throws ClassNotFoundException, SQLException {

    Connection connection;

    connection = dataBaseConfig.getConnection();

    assertThat(connection).isNotNull();
  }
}
