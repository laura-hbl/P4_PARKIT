package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.integration.config.DataBasePrepareService;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static final String REG_NUMBER = "AB125XY";

    private static ParkingSpotDao parkingSpotDao = new ParkingSpotDao();
    private static TicketDao ticketDao = new TicketDao();
    private static DataBasePrepareService dataBasePrepareService;
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() {
        parkingSpotDao.setDataBaseConfig(dataBaseTestConfig);
        ticketDao.setDataBaseConfig(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void setUpPerTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(REG_NUMBER);
        dataBasePrepareService.clearDataBaseEntries();
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDao, ticketDao);
    }

    @AfterAll
    public static void tearDown() {
    }

    @Test
    @Tag("Incoming")
    @DisplayName("Checks that a ticket is actually saved in DB and Parking table is updated with availability")
    public void givenARegistrationNumber_whenProcessIncomingVehicle_thenNewTicketIsCreatedAndParkingUpdated() {
        parkingService.processIncomingVehicle();

        int nextAvailableSpot = parkingSpotDao.getNextAvailableSpot(ParkingType.CAR);
        Ticket ticket = ticketDao.getTicket(REG_NUMBER);

        // Checks ticket saved in DB
        assertThat(ticket).isNotNull();
        // Checks vehicle parking spot updated with availability
        assertThat(nextAvailableSpot).isEqualTo(2);
    }

    @Test
    @Tag("IncomingAndExiting")
    @DisplayName("Checks that the fare generated and out time are populated correctly in the DB")
    public void givenARegistrationNumber_whenProcessExitingVehicle_thenFareIsGeneratedAndOutTimePopulated() throws InterruptedException{
        parkingService.processIncomingVehicle();
        Thread.sleep(500);
        parkingService.processExitingVehicle();

        Ticket ticket = ticketDao.getTicket(REG_NUMBER);
        double fare = ticket.getPrice();

        // Checks fare is populated
        assertThat(fare).isEqualTo(0);
        assertThat(fare).isNotNull();
        // Checks out time is populated
        assertThat(ticket.getOutTime()).isNotNull();
    }

    @Test
    @Tag("RegularUserExiting")
    @DisplayName("Checks that a user is considered as recurrent user when entering the parking for the second time")
    public void givenARegistrationNumberOfRecurringUser_whenGetIsRecurringUser_thenReturnTrue() throws InterruptedException {
        parkingService.processIncomingVehicle();
        Thread.sleep(500);
        parkingService.processExitingVehicle();
        Thread.sleep(500);
        parkingService.processIncomingVehicle();

        boolean isRecurringUser = ticketDao.isRecurringUser(REG_NUMBER);

        assertThat(isRecurringUser).isEqualTo(true);
    }
}
