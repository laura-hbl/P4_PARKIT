package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static final String REG_NUMBER = "AB125XY";

    @Mock
    private InputReaderUtil inputReaderUtil;
    @Mock
    private ParkingSpotDao parkingSpotDao;
    @Mock
    private TicketDao ticketDao;

    private ParkingService parkingService;

    @BeforeEach
    public void setUp() {
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDao, ticketDao);
    }

    private void setupParkingServiceMocks(ParkingSpot parkingSpot, Ticket ticket) {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(REG_NUMBER);
        when(ticketDao.getTicket(REG_NUMBER)).thenReturn(ticket);
        when(ticketDao.isRecurringUser(REG_NUMBER)).thenReturn(false);
        when(ticketDao.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDao.updateParking(parkingSpot)).thenReturn(true);
    }

    @Test
    @Tag("ExitingVehicle")
    @DisplayName("Given exiting vehicle, when processExitingVehicle, then vehicle exit process should be done in correct order")
    public void givenExitingVehicle_whenProcessExitingVehicle_thenVehicleProcessDoneInOrder() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 0, LocalDateTime.now().minusMinutes(60), null);
        setupParkingServiceMocks(parkingSpot, ticket);

        parkingService.processExitingVehicle();

        InOrder inOrder = inOrder(inputReaderUtil, parkingSpotDao, ticketDao);
        inOrder.verify(inputReaderUtil).readVehicleRegistrationNumber();
        inOrder.verify(ticketDao).getTicket(REG_NUMBER);
        inOrder.verify(ticketDao).isRecurringUser(REG_NUMBER);
        inOrder.verify(ticketDao).updateTicket(any(Ticket.class));
        inOrder.verify(parkingSpotDao).updateParking(any(ParkingSpot.class));
    }

    @Test
    @Tag("ExitingVehicle")
    @DisplayName("Given a ticket, when processExitingVehicle, then parking spot availability should be updated")
    public void givenATicket_whenProcessExitingVehicle_thenParkingSpotIsUpdated() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 0, LocalDateTime.now(), null);

        setupParkingServiceMocks(parkingSpot, ticket);
        parkingService.processExitingVehicle();

        assertThat(parkingSpot.isAvailable()).isTrue();
    }

    @Test
    @Tag("ExitingVehicle")
    @DisplayName("Given regular user, when processExitingVehicle, then ticket should be updated with out time and fare with discount applied")
    public void givenAOneHourTicket_whenProcessExitingVehicleOfReturningUser_thenTicketIsUpdated() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 0, LocalDateTime.now().minusMinutes(60), null);

        setupParkingServiceMocks(parkingSpot, ticket);
        when(ticketDao.isRecurringUser(REG_NUMBER)).thenReturn(true);

        parkingService.processExitingVehicle();

        assertThat(ticket.getOutTime()).isNotNull();
        assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR - (Fare.DISCOUNT * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @Tag("ExitingVehicle")
    @DisplayName("Given new user, when processExitingVehicle, then ticket should be updated with out time and fare without discount")
    public void givenAOneHourTicket_whenProcessExitingVehicleNewUser_thenTicketIsUpdated() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(1, parkingSpot, REG_NUMBER, 0, LocalDateTime.now().minusMinutes(60), null);
        setupParkingServiceMocks(parkingSpot, ticket);

        parkingService.processExitingVehicle();

        assertThat(ticket.getOutTime()).isNotNull();
        assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @Tag("IncomingVehicle")
    @DisplayName("Given incoming vehicle, when processIncomingVehicle, then vehicle entry process should be done in correct order")
    public void givenIncomingVehicle_whenProcessIncomingVehicle_thenVehicleProcessDoneInOrder() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDao.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(REG_NUMBER);
        when(ticketDao.isRecurringUser(REG_NUMBER)).thenReturn(false);

        parkingService.processIncomingVehicle();

        InOrder inOrder = inOrder(inputReaderUtil, parkingSpotDao, ticketDao);
        inOrder.verify(inputReaderUtil).readSelection();
        inOrder.verify(parkingSpotDao).getNextAvailableSpot(ParkingType.CAR);
        inOrder.verify(parkingSpotDao).updateParking(any(ParkingSpot.class));
        inOrder.verify(inputReaderUtil).readVehicleRegistrationNumber();
        inOrder.verify(ticketDao).saveTicket(any(Ticket.class));
        inOrder.verify(ticketDao).isRecurringUser(REG_NUMBER);
    }

    @Test
    @Tag("GetNextParkingNumberIfAvailable")
    @DisplayName("Given an expected car ParkingSpot, when getNextParkingNumberIfAvailable, then parkingSpot should match expected car parkingSpot")
    public void givenACarParkingSpot_whenGetNextParkingNumberIfAvailable_thenReturnExpectedParkingSpot() {
        ParkingSpot expectedParkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDao.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        verify(parkingSpotDao).getNextAvailableSpot(ParkingType.CAR);
        assertThat(parkingSpot).isEqualTo(expectedParkingSpot);
    }

    @Test
    @Tag("GetNextParkingNumberIfAvailable")
    @DisplayName("Given an expected bike parkingSpot, when getNextParkingNumberIfAvailable, then parkingSpot should match expected bike parkingSpot")
    public void givenABikeParkingSpot_whenGetNextParkingNumberIfAvailable_thenReturnExpectedParkingSpot() {
        ParkingSpot expectedParkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDao.getNextAvailableSpot(ParkingType.BIKE)).thenReturn(4);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        verify(parkingSpotDao).getNextAvailableSpot(ParkingType.BIKE);
        assertThat(parkingSpot).isEqualTo(expectedParkingSpot);
    }
}
