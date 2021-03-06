package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.util.InputReaderUtil;
import com.parkit.parkingsystem.util.ScannerWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Loads and displays the main application menu.
 *
 * @author Laura
 */
public final class InteractiveShell {

  /**
   * InteractiveShell logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("InteractiveShell");

  /**
   * Empty constructor of class InteractiveShell.
   */
  private InteractiveShell() {
  }

  /**
   * Manages the main application interface. Calls loadMenu() to display menu
   * items, then after user entry, calls corresponding parkingService method or
   * shuts down the application.
   */
  public static void loadInterface() {
    LOGGER.info("Welcome to Parking System!");

    boolean continueApp = true;

    ScannerWrapper scannerWrapper = new ScannerWrapper();
    InputReaderUtil inputReaderUtil = new InputReaderUtil(scannerWrapper);
    TicketDao ticketDao = new TicketDao();
    ParkingSpotDao parkingSpotDao = new ParkingSpotDao();
    ParkingService parkingService = new ParkingService(inputReaderUtil,
        parkingSpotDao, ticketDao);

    while (continueApp) {
      loadMenu();
      int option = inputReaderUtil.readSelection();
      final int incomingVehicle = 1;
      final int exitingVehicle = 2;
      final int exitingSystem = 3;

      switch (option) {

        case incomingVehicle:
          parkingService.processIncomingVehicle();
          break;

        case exitingVehicle:
          parkingService.processExitingVehicle();
          break;

        case exitingSystem:
          LOGGER.info("Exiting from the system!");
          continueApp = false;
          break;

        default:
          LOGGER.info("Unsupported option. Please enter a number "
              + "corresponding to the provided menu");
      }
    }
  }

  /**
   * Displays the app main menu.
   */
  private static void loadMenu() {
    LOGGER.info("Please select an option. Simply enter the number to "
        + "choose an action");
    LOGGER.info("1 New Vehicle Entering - Allocate Parking Space");
    LOGGER.info("2 Vehicle Exiting - Generate Ticket Price");
    LOGGER.info("3 Shutdown System");
  }
}


