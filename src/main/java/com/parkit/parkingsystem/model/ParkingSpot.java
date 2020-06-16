package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * Permits the storage and retrieving values from the parking
 * table (prod database).
 *
 * @author Laura
 */
public class ParkingSpot {

  /**
   * The number of a parking spot.
   */
  private int number;

  /**
   * The parking type (CAR or BIKE).
   */
  private ParkingType parkingType;

  /**
   * Availability of a parking spot.
   */
  private boolean isAvailable;

  /**
   * Constructor of class ParkingSpot.
   * Initialize number, parkingType and isAvailable.
   *
   * @param num the number of a parking spot
   * @param type the parking type (car or bike)
   * @param available tells if parking spot is used or free
   */
  public ParkingSpot(final int num, final ParkingType type,
                     final boolean available) {
    this.number = num;
    this.parkingType = type;
    this.isAvailable = available;
  }

  /**
   * Getter of ParkingSpot.number.
   *
   * @return the number of a parking spot
   */
  public int getNumber() {
    return number;
  }

  /**
   * Setter of ParkingSpot.number.
   *
   * @param num the number of parking spot to set
   */
  public void setNumber(final int num) {
    this.number = num;
  }

  /**
   * Getter of the ParkingSpot.parkingType.
   *
   * @return the appropriate parkingType: Car or bike
   */
  public ParkingType getParkingType() {
    return parkingType;
  }

  /**
   * Setter of the ParkingSpot.parkingType.
   *
   * @param type parkingType: car or bike
   */
  public void setParkingType(final ParkingType type) {
    this.parkingType = type;
  }

  /**
   * Getter of ParkingSpot.isAvailable.
   *
   * @return true if that parkingSpot is available false if occupied
   */
  public boolean isAvailable() {
    return isAvailable;
  }

  /**
   * Setter of ParkingSpot.isAvailable.
   *
   * @param available availability of the parking spot
   */
  public void setAvailable(final boolean available) {
    isAvailable = available;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ParkingSpot that = (ParkingSpot) o;
    return number == that.number;
  }

  @Override
  public final int hashCode() {
    return number;
  }
}
