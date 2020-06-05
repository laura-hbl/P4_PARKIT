package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.TicketDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

    private static final String REG_NUMBER = "ABCDEF";

    private TicketDAO ticketDAO;

    @Mock
    private static DataBaseConfig dataBaseConfig;
    @Mock
    private static Connection connection;
    @Mock
    private static PreparedStatement preparedStatement;
    @Mock
    private static ResultSet resultSet;


    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        ticketDAO = new TicketDAO(dataBaseConfig);
    }

    @Test
    public void givenARegistrationNumber_whenGetIsRecurringUser_thenReturnTrue() throws Exception {

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(2);

        boolean isRecurrentUser = ticketDAO.isRecurringUser(REG_NUMBER);

        assertThat(isRecurrentUser).isTrue();
    }

    @Test
    public void givenARegistrationNumber_whenGetIsRecurringUser_thenReturnFalse() throws Exception {

        boolean isRecurrentUser = ticketDAO.isRecurringUser(REG_NUMBER);

        assertThat(isRecurrentUser).isFalse();
    }

}
