package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


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
    public void givenATicket_whenSaveTicket_thenTicketIsSaved() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket(0, parkingSpot, REG_NUMBER, 0, LocalDateTime.now(), null);
        when(preparedStatement.execute()).thenReturn(true);

        boolean isTicketSaved = ticketDAO.saveTicket(ticket);

        InOrder inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setInt(1, ticket.getParkingSpot().getNumber());
        inOrder.verify(preparedStatement).setString(2, ticket.getVehicleRegNumber());
        inOrder.verify(preparedStatement).setDouble(3, ticket.getPrice());
        inOrder.verify(preparedStatement).setObject(4, ticket.getInTime());
        inOrder.verify(preparedStatement).setObject(5, ticket.getOutTime());
        inOrder.verify(preparedStatement).execute();
        assertThat(isTicketSaved).isTrue();
    }

    @Test
    public void givenATicket_whenSaveTicketNotExecuted_thenTicketIsNotSaved() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket(0, parkingSpot, REG_NUMBER, 0, LocalDateTime.now(),null);
        when(preparedStatement.execute()).thenReturn(false);

        boolean isTicketSaved = ticketDAO.saveTicket(ticket);

        assertThat(isTicketSaved).isFalse();
    }

    @Test
    public void givenATicket_whenUpdateTicket_thenTicketIsUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(0, parkingSpot, REG_NUMBER, 1.5, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60));
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isTicketUpdated = ticketDAO.updateTicket(ticket);

        InOrder inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setDouble(1, ticket.getPrice());
        inOrder.verify(preparedStatement).setObject(2, ticket.getOutTime());
        inOrder.verify(preparedStatement).setInt(3, ticket.getId());
        inOrder.verify(preparedStatement).executeUpdate();
        assertThat(isTicketUpdated).isTrue();
    }

    @Test
    public void givenATicket_whenUpdateTicketNotExecuted_thenTicketIsNotUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(0, parkingSpot, REG_NUMBER, 1.5, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60));
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean isTicketUpdated = ticketDAO.updateTicket(ticket);

        assertThat(isTicketUpdated).isFalse();
    }

    @Test
    public void givenARegistrationNumber_whenGetIsRecurringUser_thenReturnTrue() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(2);

        boolean isRecurrentUser = ticketDAO.isRecurringUser(REG_NUMBER);

        verify(preparedStatement).setString(1, REG_NUMBER);
        verify(preparedStatement).executeQuery();
        verify(resultSet).next();
        assertThat(isRecurrentUser).isTrue();
    }

    @Test
    public void givenARegistrationNumber_whenGetIsRecurringUser_thenReturnFalse() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean isRecurrentUser = ticketDAO.isRecurringUser(REG_NUMBER);

        assertThat(isRecurrentUser).isFalse();
    }

    @Test
    public void givenARegistrationNumber_whenGetTicket_ThenReturnExpectedTicket() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket expectedTicket = new Ticket(1, parkingSpot, REG_NUMBER, 1.5, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60));

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        // Mockito requires 0 here to allow for when with different params values
        when(resultSet.getInt(eq(0))).thenReturn(expectedTicket.getParkingSpot().getNumber());
        when(resultSet.getInt(eq(1))).thenReturn(expectedTicket.getParkingSpot().getNumber());
        when(resultSet.getInt(eq(2))).thenReturn(expectedTicket.getId());
        when(resultSet.getDouble(3)).thenReturn(expectedTicket.getPrice());
        // Mockito requires 0 here to allow for when with different params values
        when(resultSet.getTimestamp(eq(0))).thenReturn(Timestamp.valueOf(expectedTicket.getInTime()));
        when(resultSet.getTimestamp(eq(4))).thenReturn(Timestamp.valueOf(expectedTicket.getInTime()));
        when(resultSet.getTimestamp(eq(5))).thenReturn(Timestamp.valueOf(expectedTicket.getOutTime()));
        when(resultSet.getString(6)).thenReturn(expectedTicket.getParkingSpot().getParkingType().toString());

        Ticket ticket = ticketDAO.getTicket(REG_NUMBER);

        assertThat(expectedTicket).isEqualTo(ticket);
    }

    @Test
    public void givenARegistrationNumber_whenGetTicketWithNullResult_thenReturnNull() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Ticket ticket = ticketDAO.getTicket(REG_NUMBER);

        assertThat(ticket).isNull();
    }
}