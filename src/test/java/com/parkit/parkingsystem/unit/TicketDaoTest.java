package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DataBaseParameters;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TicketDaoTest {

    private static final String REG_NUMBER = "AB125XY";

    private TicketDao ticketDao = new TicketDao();

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
        ticketDao.setDataBaseConfig(dataBaseConfig);
    }

    @Test
    @Tag("SaveTicket")
    @DisplayName("Given a ticket, when saveTicket, then ticket should be saved correctly")
    public void givenATicket_whenSaveTicket_thenTicketIsSaved() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 0, LocalDateTime.now(), null);
        when(preparedStatement.execute()).thenReturn(true);

        boolean isTicketSaved = ticketDao.saveTicket(ticket);

        InOrder inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setInt(DataBaseParameters.ONE, ticket.getParkingSpot().getNumber());
        inOrder.verify(preparedStatement).setString(DataBaseParameters.TWO, ticket.getVehicleRegNumber());
        inOrder.verify(preparedStatement).setDouble(DataBaseParameters.THREE, ticket.getPrice());
        inOrder.verify(preparedStatement).setObject(DataBaseParameters.FOUR, ticket.getInTime());
        inOrder.verify(preparedStatement).setObject(DataBaseParameters.FIVE, ticket.getOutTime());
        inOrder.verify(preparedStatement).execute();
        assertThat(isTicketSaved).isTrue();
    }

    @Test
    @Tag("SaveTicket")
    @DisplayName("Given a ticket, when saveTicket is not executed, then ticket should not be saved")
    public void givenATicket_whenSaveTicketNotExecuted_thenTicketIsNotSaved() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 0, LocalDateTime.now(), null);
        when(preparedStatement.execute()).thenReturn(false);

        boolean isTicketSaved = ticketDao.saveTicket(ticket);

        assertThat(isTicketSaved).isFalse();
    }

    @Test
    @Tag("UpdateTicket")
    @DisplayName("Given a ticket, when updateTicket, then ticket should be updated correctly")
    public void givenATicket_whenUpdateTicket_thenTicketIsUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 1.5, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60));
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isTicketUpdated = ticketDao.updateTicket(ticket);

        InOrder inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setDouble(DataBaseParameters.ONE, ticket.getPrice());
        inOrder.verify(preparedStatement).setObject(DataBaseParameters.TWO, ticket.getOutTime());
        inOrder.verify(preparedStatement).setInt(DataBaseParameters.THREE, ticket.getId());
        inOrder.verify(preparedStatement).executeUpdate();
        assertThat(isTicketUpdated).isTrue();
    }

    @Test
    @Tag("UpdateTicket")
    @DisplayName("Given a ticket, when updateTicket is not executed, then ticket should not be updated")
    public void givenATicket_whenUpdateTicketNotExecuted_thenTicketIsNotUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 1.5, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60));
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean isTicketUpdated = ticketDao.updateTicket(ticket);

        assertThat(isTicketUpdated).isFalse();
    }

    @Test
    @Tag("IsRecurringUser")
    @DisplayName("Given a licence number with more than one ticket associated with it, then isRecurringUser should return true")
    public void givenARegistrationNumberAssociatedWithMoreThanOneTicket_whenGetIsRecurringUser_thenReturnTrue() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(DataBaseParameters.ONE)).thenReturn(2);

        boolean isRecurrentUser = ticketDao.isRecurringUser(REG_NUMBER);

        InOrder inOrder = inOrder(preparedStatement, resultSet);
        inOrder.verify(preparedStatement).setString(DataBaseParameters.ONE, REG_NUMBER);
        inOrder.verify(preparedStatement).executeQuery();
        inOrder.verify(resultSet).next();
        inOrder.verify(resultSet).getInt(DataBaseParameters.ONE);
        assertThat(isRecurrentUser).isTrue();
    }

    @Test
    @Tag("IsRecurringUser")
    @DisplayName("Given a licence number with only one ticket associated with it, then isRecurringUser should return false")
    public void givenARegistrationAssociatedWithOnlyOneTicket_whenGetIsRecurringUser_thenReturnFalse() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(DataBaseParameters.ONE)).thenReturn(0);

        boolean isRecurrentUser = ticketDao.isRecurringUser(REG_NUMBER);

        assertThat(isRecurrentUser).isFalse();
    }


    @Test
    @Tag("IsRecurringUser")
    @DisplayName("Given a licence number, when no resultSet, then isRecurringUser should return false")
    public void givenARegistrationNumber_whenGetIsRecurringUser_thenReturnFalse() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        boolean isRecurrentUser = ticketDao.isRecurringUser(REG_NUMBER);

        assertThat(isRecurrentUser).isFalse();
    }

    @Test
    @Tag("GetTicket")
    @DisplayName("Given an expected ticket, when getTicket, then ticket should match expected ticket")
    public void givenARegistrationNumber_whenGetTicket_ThenReturnExpectedTicket() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket expectedTicket = new Ticket(1, parkingSpot, REG_NUMBER, 1.5, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60));

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        // Mockito requires 0 here to allow for when with different params values
        when(resultSet.getInt(eq(DataBaseParameters.ZERO))).thenReturn(expectedTicket.getParkingSpot().getNumber());
        when(resultSet.getInt(eq(DataBaseParameters.ONE))).thenReturn(expectedTicket.getParkingSpot().getNumber());
        when(resultSet.getInt(eq(DataBaseParameters.TWO))).thenReturn(expectedTicket.getId());
        when(resultSet.getDouble(DataBaseParameters.THREE)).thenReturn(expectedTicket.getPrice());
        // Mockito requires 0 here to allow for when with different params values
        when(resultSet.getTimestamp(eq(DataBaseParameters.ZERO))).thenReturn(Timestamp.valueOf(expectedTicket.getInTime()));
        when(resultSet.getTimestamp(eq(DataBaseParameters.FOUR))).thenReturn(Timestamp.valueOf(expectedTicket.getInTime()));
        when(resultSet.getTimestamp(eq(DataBaseParameters.FIVE))).thenReturn(Timestamp.valueOf(expectedTicket.getOutTime()));
        when(resultSet.getString(DataBaseParameters.SIX)).thenReturn(expectedTicket.getParkingSpot().getParkingType().toString());

        Ticket ticket = ticketDao.getTicket(REG_NUMBER);

        assertThat(expectedTicket).isEqualTo(ticket);
    }

    @Test
    @Tag("GetTicket")
    @DisplayName("Given a licence number, when no resultSet, then getTicket should return null")
    public void givenARegistrationNumber_whenGetTicketWithNullResult_thenReturnNull() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Ticket ticket = ticketDao.getTicket(REG_NUMBER);

        assertThat(ticket).isNull();
    }
}