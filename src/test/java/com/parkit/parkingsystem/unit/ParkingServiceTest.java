package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static final String REG_NUMBER = "ABCDEF";

    @Mock
    private InputReaderUtil inputReaderUtil;
    @Mock
    private ParkingSpotDAO parkingSpotDAO;
    @Mock
    private TicketDAO ticketDAO;

    private ParkingService parkingService;

    @BeforeEach
    public void setUp(){
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    @Test
    public void givenATicket_whenProcessExitingVehicle_thenParkingSpotIsUpdated() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(0, parkingSpot, REG_NUMBER, 0, LocalDateTime.now(), LocalDateTime.now().plusMinutes(60));

        setupParkingServiceMocks(parkingSpot, ticket);
        parkingService.processExitingVehicle();

        assertThat(parkingSpot.isAvailable()).isTrue();
        verify(parkingSpotDAO).updateParking(parkingSpot);
    }

    @Test
    public void givenIncomingVehicle_whenProcessIncomingVehicle_thenVehicleProcessDoneInOrder() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(REG_NUMBER);
        when(ticketDAO.isRecurringUser(REG_NUMBER)).thenReturn(false);

        parkingService.processIncomingVehicle();

        InOrder inOrder = inOrder(inputReaderUtil, parkingSpotDAO, ticketDAO);
        inOrder.verify(inputReaderUtil).readSelection();
        inOrder.verify(parkingSpotDAO).getNextAvailableSpot(ParkingType.CAR);
        inOrder.verify(parkingSpotDAO).updateParking(any(ParkingSpot.class));
        inOrder.verify(inputReaderUtil).readVehicleRegistrationNumber();
        inOrder.verify(ticketDAO).saveTicket(any(Ticket.class));
        inOrder.verify(ticketDAO).isRecurringUser(REG_NUMBER);
    }

    @Test
    public void givenAOneHourTicket_whenProcessExitingVehicleOfReturningUser_thenTicketIsUpdated() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(0, parkingSpot, REG_NUMBER, 0, LocalDateTime.now().minusMinutes(60), LocalDateTime.now());

        setupParkingServiceMocks(parkingSpot, ticket);
        when(ticketDAO.isRecurringUser(REG_NUMBER)).thenReturn(true);

        parkingService.processExitingVehicle();

        assertThat(ticket.getOutTime()).isNotNull();
        assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR - (Fare.DISCOUNT * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    public void givenAOneHourTicket_whenProcessExitingVehicleNewUser_thenTicketIsUpdated() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket(0, parkingSpot, REG_NUMBER, 0, LocalDateTime.now().minusMinutes(60), LocalDateTime.now());

        setupParkingServiceMocks(parkingSpot, ticket);
        when(ticketDAO.isRecurringUser(REG_NUMBER)).thenReturn(false);

        parkingService.processExitingVehicle();

        assertThat(ticket.getOutTime()).isNotNull();
        assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void givenACarParkingSpot_whenGetNextParkingNumberIfAvailable_thenReturnExpectedParkingSpot() {
        ParkingSpot expectedParkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR)).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertThat(parkingSpot).isEqualTo(expectedParkingSpot);
    }

    @Test
    public void givenABikeParkingSpot_whenGetNextParkingNumberIfAvailable_thenReturnExpectedParkingSpot() {
        ParkingSpot expectedParkingSpot = new ParkingSpot(4, ParkingType.BIKE, true);
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSpot(ParkingType.BIKE)).thenReturn(4);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

        assertThat(parkingSpot).isEqualTo(expectedParkingSpot);
    }

    private void setupParkingServiceMocks(ParkingSpot parkingSpot, Ticket ticket) {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(REG_NUMBER);
        when(ticketDAO.getTicket(REG_NUMBER)).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(true);
        when(parkingSpotDAO.updateParking(parkingSpot)).thenReturn(true);
    }
}
