package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static final String REG_NUMBER = "ABCDEF";

    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
        ticketDAO = new TicketDAO(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() {
        dataBasePrepareService.clearDataBaseEntries();
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(REG_NUMBER);
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    public void givenARegistrationNumber_whenProcessIncomingVehicle_thenNewTicketIsCreatedAndParkingUpdated() {
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();
        int nextAvailableSpot = parkingSpotDAO.getNextAvailableSpot(ParkingType.CAR);
        Ticket ticket = ticketDAO.getTicket(REG_NUMBER);

        assertThat(ticket).isNotNull();
        assertThat(nextAvailableSpot).isEqualTo(2);
    }

    @Test
    public void givenARegistrationNumber_whenProcessExitingVehicle_thenFareIsGeneratedAndOutTimePopulated() {
        //TODO: check that the fare generated and out-time are populated correctly in the database
        givenARegistrationNumber_whenProcessIncomingVehicle_thenNewTicketIsCreatedAndParkingUpdated();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        Ticket ticket = ticketDAO.getTicket(REG_NUMBER);
        double fare = ticket.getPrice();

        assertThat(fare).isEqualTo(0);
        assertThat(ticket.getOutTime()).isNotNull();
    }

    @Test
    public void givenARegistrationNumberOfRecurringUer_whenGetIsRecurringUser_thenReturnTrue() {
        givenARegistrationNumber_whenProcessExitingVehicle_thenFareIsGeneratedAndOutTimePopulated();
        givenARegistrationNumber_whenProcessIncomingVehicle_thenNewTicketIsCreatedAndParkingUpdated();

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        boolean isRecurringUser = ticketDAO.isRecurringUser(REG_NUMBER);

        assertThat(isRecurringUser).isEqualTo(true);
    }
}
