package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputReaderUtil {

    private final ScannerWrapper scannerWrapper;
    private static final Logger logger = LogManager.getLogger("InputReaderUtil");

    public InputReaderUtil(ScannerWrapper scannerWrapper) {
        this.scannerWrapper = scannerWrapper;
    }

    public int readSelection() {
        int input = -1;

        try {
            input = Integer.parseInt(scannerWrapper.nextLine());
        } catch (Exception e) {
            logger.error("Error while reading user input from Shell", e);
        }

        return input;
    }

    public String readVehicleRegistrationNumber() {
        String vehicleRegNumber = scannerWrapper.nextLine();

        if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
            logger.error("Error while reading user input from Shell");
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
            throw new IllegalArgumentException("Invalid input provided");
        }

        return vehicleRegNumber;
    }

}
