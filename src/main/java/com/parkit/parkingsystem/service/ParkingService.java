package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.FareCalculatorHelper;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages parking entry and exit process.
 *
 * @author Laura
 */
public class ParkingService {

  /**
   * ParkingService logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("ParkingService");

  /**
   * InputReaderUtil object.
   */
  private final InputReaderUtil inputReaderUtil;

  /**
   * ParkingSpotDao object.
   */
  private final ParkingSpotDao parkingSpotDao;

  /**
   * TicketDao object.
   */
  private final TicketDao ticketDao;


  /**
   * Constructor of class ParkingService.
   * Initialize inputReader, parkingSpotDao and ticketDao.
   *
   * @param inputReader    InputReaderUtil object
   * @param daoParkingSpot ParkingSpotDao object
   * @param daoTicket      TicketDao object
   */
  public ParkingService(final InputReaderUtil inputReader, final ParkingSpotDao
      daoParkingSpot, final TicketDao daoTicket) {
    this.inputReaderUtil = inputReader;
    this.parkingSpotDao = daoParkingSpot;
    this.ticketDao = daoTicket;
  }

  /**
   * Manages the vehicle incoming process.
   */
  public void processIncomingVehicle() {

    try {
      /* The system fetches the next available spot depending on the type of
      vehicle of the user. */
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();

      if (parkingSpot != null && parkingSpot.getNumber() > 0) {
        parkingSpot.setAvailable(false);

        // Allot this parking space and mark it's availability as false
        parkingSpotDao.updateParking(parkingSpot);
        String vehicleRegNumber = getVehicleRegNumber();

        Ticket ticket = new Ticket();
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        // MYSQL has problems with saving milliseconds in the database.
        LocalDateTime inTime = LocalDateTime.now().minusSeconds(1);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        /* Ticket is saved in DB with parkingSpot, parkingType,
        vehicleRegNumber, InTime and OutTime. */
        ticketDao.saveTicket(ticket);

        /* The system checks whether the user has entered the parking
           previously. */
        if (ticketDao.isRecurringUser(vehicleRegNumber)) {
          LOGGER.info("Welcome back! As a recurring user of our"
              + " parking lot, you'll benefit from a 5% discount.");
        }

        LOGGER.info("Generated Ticket and saved in DB");
        LOGGER.info("Please park your vehicle in spot number: "
            + parkingSpot.getNumber());
        LOGGER.info("Recorded in-time for vehicle number:"
            + vehicleRegNumber + " is: " + inTime.format(DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")));
      }
    } catch (Exception e) {
      LOGGER.error("Unable to process incoming vehicle", e);
    }
  }

  /**
   * Calls InputReaderUtil's readSelection method.
   * Asks the user for his/her vehicle type.
   *
   * @return the provided ParkingType enum
   */
  private ParkingType getVehicleType() {
    LOGGER.info("Please select vehicle type from menu");
    LOGGER.info("1 CAR");
    LOGGER.info("2 BIKE");
    int input = inputReaderUtil.readSelection();

    switch (input) {

      case 1:
        return ParkingType.CAR;

      case 2:
        return ParkingType.BIKE;

      default:
        LOGGER.error("Incorrect input provided. Please enter "
            + "valid number");
        throw new IllegalArgumentException("Entered input is invalid");
    }
  }

  /**
   * Checks in database if there is any available parking spot for this type
   * of vehicle.
   *
   * @return the available parking spot or null parking spot if the system
   *     fails to fetch an available parking spot
   */
  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber;
    ParkingSpot parkingSpot = null;

    try {
      ParkingType parkingType = getVehicleType();
      parkingNumber = parkingSpotDao.getNextAvailableSpot(parkingType);

      if (parkingNumber > 0) {
        parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
      } else {
        throw new SQLException("Error fetching parking number from DB. "
        + "Parking spots might be full");
      }
    } catch (IllegalArgumentException ie) {
      LOGGER.error("Error parsing user input for type of vehicle", ie);
    } catch (Exception e) {
      LOGGER.error("Error fetching next available parking spot", e);
    }

    return parkingSpot;
  }

  /**
   * Calls InputReaderUtil's readVehicleRegistrationNumber method.
   * Asks the user for his/her licence plate number
   *
   * @return the licence plate number provided
   */
  private String getVehicleRegNumber() {
    LOGGER.info("Please type the vehicle registration number"
         + " and press enter key");
    return inputReaderUtil.readVehicleRegistrationNumber();
  }

  /**
   * Checks whether the given licence plate number is saved more than once
   * in database, if so the user can benefit a 5% discount on the price when
   * exiting the parking.
   *
   * @param vehicleRegNumber the user's licence plate number
   * @return 5% discount if it's a recurrent user, 0 if it's a new user.
   */
  private double getDiscount(final String vehicleRegNumber) {

    if (ticketDao.isRecurringUser(vehicleRegNumber)) {
      return Fare.DISCOUNT;
    }

    return 0;
  }

  /**
   * Manages the vehicle exit process.
   */
  public void processExitingVehicle() {
    try {
      String vehicleRegNumber = getVehicleRegNumber();
      // Retrieve the last ticket saved in Database associated with
      // this registration number.
      Ticket ticket = ticketDao.getTicket(vehicleRegNumber);
      LocalDateTime outTime = LocalDateTime.now();
      ticket.setOutTime(outTime);
      // Users get a 5% discount when they use the parking garage regularly.
      double discount = getDiscount(vehicleRegNumber);
      double fare = FareCalculatorHelper.getCalculatedFare(ticket, discount);

      ticket.setPrice(fare);

      // Ticket is updated in the Database with the fare generated and out-time.
      if (ticketDao.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);

        // Release this parking space and mark it's availability as true
        if (parkingSpotDao.updateParking(parkingSpot)) {
          DecimalFormat formatPrice = new DecimalFormat("0.00");
          LOGGER.info("Please pay the parking fare: "
              + formatPrice.format(ticket.getPrice()));
          LOGGER.info("Recorded out-time for vehicle number: "
              + ticket.getVehicleRegNumber() + " is: "
              + ticket.getOutTime().format(DateTimeFormatter.ofPattern(
              "yyyy-MM-dd HH:mm:ss")));
        }
      } else {
        LOGGER.error("Unable to update ticket information."
            + " Error occurred");
      }
    } catch (Exception e) {
      LOGGER.error("Unable to process exiting vehicle", e);
    }
  }
}
