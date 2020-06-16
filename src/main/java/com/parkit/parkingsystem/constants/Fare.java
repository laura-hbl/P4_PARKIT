package com.parkit.parkingsystem.constants;


/**
 * Contains parking rates per hour, discount rate and free parking time.
 *
 * @author Laura
 */
public final class Fare {

  /**
   * Empty constructor of class Fare.
   */
  private Fare() {
  }

  /**
   * Parking rate per hour for bike.
   */
  public static final double BIKE_RATE_PER_HOUR = 1.0;

  /**
   * Parking rate per hour for car.
   */
  public static final double CAR_RATE_PER_HOUR = 1.5;

  /**
   * Discount rate for recurrent user.
   */
  public static final double DISCOUNT = 0.05;

  /**
   * Free parking time.
   */
  public static final double FREE_PARKING_TIME = 0.5;
}
