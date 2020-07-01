package com.parkit.parkingsystem.util;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Calculates the price to be paid by the user when exiting the parking.
 *
 * @author Laura
 */
public final class FareCalculatorUtil {

  /**
   * Empty constructor of class FareCalculatorUtil.
   */
  private FareCalculatorUtil() {
  }

  /**
   * Calculates the fare based on parking time, vehicle type and discount rate.
   *
   * @param ticket the ticket from which calculation is done.
   * @param discount discount based on if it's a recurrent user or new user.
   * @return the price to pay after calculation.
   */
  public static double getCalculatedFare(final Ticket ticket, final double
      discount) {

    if (ticket.getOutTime() == null || (ticket.getOutTime()
        .isBefore(ticket.getInTime()))) {
      throw new IllegalArgumentException("Out time provided is incorrect:"
      + ticket.getOutTime());
    }

    LocalDateTime inTime = ticket.getInTime();
    LocalDateTime outTime = ticket.getOutTime();
    final int toHour = 60;
    // Converting duration to hours.
    double duration = (double) (Duration.between(inTime, outTime)).toMinutes()
        / toHour;

    // Allow users to park for free when they stay less than 30 minutes.
    if (duration < Fare.FREE_PARKING_TIME) {
      return 0;
      /* The system calculates the fare depending on parking time, the type
      of vehicle and discount if recurrent user. */
    } else {
      switch (ticket.getParkingSpot().getParkingType()) {

        case CAR:
          return duration * Fare.CAR_RATE_PER_HOUR - (duration
            * Fare.CAR_RATE_PER_HOUR * discount);

        case BIKE:
          return duration * Fare.BIKE_RATE_PER_HOUR - (duration
            * Fare.BIKE_RATE_PER_HOUR * discount);

        default:
          throw new IllegalArgumentException("Unknown Parking Type");
      }
    }
  }
}
