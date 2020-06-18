package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reads int keyboard inputs and String registering number.
 *
 * @author Laura
 */
public class InputReaderUtil {

  /**
   * ScannerWrapper instance.
   */
  private final ScannerWrapper scannerWrapper;

  /**
   * InputReaderUtil logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("InputReaderUtil");

  /**
   * The maximum number to enter in the menu choice selection.
   */
  private static final int MAX_INPUT_SELECTION = 3;

  /**
   * Maximum number of characters allocated for a licence plate.
   */
  private static final int MAX_CHAR_REG_NUMBER = 8;

  /**
   * Constructor of class InputReaderUtil, initialize scannerWrapper.
   *
   * @param scanner scannerWrapper instance.
   */
  public InputReaderUtil(final ScannerWrapper scanner) {
    this.scannerWrapper = scanner;
  }

  /**
   * Reads user input by calling ScannerWrapper's nextLine() method.
   *
   * @return the number provided by the user if the value is correct
   *     or -1 if the value is invalid
   */
  public int readSelection() {
    try {
      int input = Integer.parseInt(scannerWrapper.nextLine());

      if (input >= 1 && input <= MAX_INPUT_SELECTION) {
        return input;
      }
    } catch (Exception e) {
      LOGGER.error("Error while reading user input from Shell", e);
      LOGGER.info("Error reading input. Please enter valid number "
          + "for proceeding further");
    }

    return -1;
  }

  /**
   * Allows the user to enter his licence plate number.
   * Reads user input by calling ScannerWrapper's nextLine() method.
   *
   * @return the vehicle reg number if the provided value is correct
   */
  public String readVehicleRegistrationNumber() {
    String vehicleRegNumber = scannerWrapper.nextLine();

    if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0
        || vehicleRegNumber.trim().length() > MAX_CHAR_REG_NUMBER) {
      LOGGER.error("Error while reading user input from Shell");
      LOGGER.info("Error reading input. Please enter a valid string "
          + "for vehicle registration number");
      throw new IllegalArgumentException("Invalid input provided");
    }

    return vehicleRegNumber;
  }
}
