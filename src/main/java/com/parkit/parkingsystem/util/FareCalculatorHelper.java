package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

public class FareCalculatorHelper {

    public static double getCalculatedFare(Ticket ticket){
        if( (ticket.getOutTime() == null || (ticket.getOutTime().isBefore(ticket.getInTime())))) {
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();
        // Converting duration to hours.
        double duration = (double) (Duration.between(inTime, outTime)).toMinutes() / 60;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                return duration * Fare.CAR_RATE_PER_HOUR;
            }
            case BIKE: {
                return duration * Fare.BIKE_RATE_PER_HOUR;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }

    }
}

