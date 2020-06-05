package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingTime;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

public final class FareCalculatorHelper {

    public static double getCalculatedFare(Ticket ticket, double discount) {

        if (ticket.getOutTime() == null || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();
        // Converting duration to hours.
        double duration = (double) (Duration.between(inTime, outTime)).toMinutes() / 60;

        // Allow users to park for free when they stay less than 30 minutes.
        if (duration < ParkingTime.FREE_PARKING_TIME) {
            return 0;
            // The system calculates the fare according to the parking time, the type of vehicle and discount.
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    return duration * Fare.CAR_RATE_PER_HOUR - (duration * Fare.CAR_RATE_PER_HOUR * discount);
                }
                case BIKE: {
                    return duration * Fare.BIKE_RATE_PER_HOUR - (duration * Fare.BIKE_RATE_PER_HOUR * discount);
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
    }
}

