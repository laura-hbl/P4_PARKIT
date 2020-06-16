package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Permits the storage and retrieving values from the ticket
 * table (prod database).
 *
 * @author Laura
 */
public class Ticket {

  /**
   * Represents the unique identifier of a ticket.
   */
  private int id;

  /**
   * The number of the parking spot allocated to the vehicle.
   */
  private ParkingSpot parkingSpot;

  /**
   * Represents the user's vehicle registration number.
   */
  private String vehicleRegNumber;

  /**
   * The price that will be paid by the user when exiting the parking.
   */
  private double price;

  /**
   * The LocalDateTime at which a user enters to the parking.
   */
  private LocalDateTime inTime;

  /**
   * The LocalDateTime at which a user leaves the parking.
   */
  private LocalDateTime outTime;

  /**
   * Constructor of class Ticket.
   * Initialize id, parkingSpot, vehicleRegNumber, price, inTime and OutTime.
   *
   * @param idTicket ticket id.
   * @param spot parking spot.
   * @param regNum vehicle registration number.
   * @param fare price to pay when exit parking.
   * @param comingTime time when incoming parking.
   * @param exitTime time when exit parking.
   */
  public Ticket(final int idTicket, final ParkingSpot spot, final String regNum,
                final double fare, final LocalDateTime comingTime,
                final LocalDateTime exitTime) {
    this.id = idTicket;
    this.parkingSpot = spot;
    this.vehicleRegNumber = regNum;
    this.price = fare;
    this.inTime = comingTime;
    this.outTime = exitTime;
  }

  /**
   * Empty constructor of class Ticket.
   */
  public Ticket() {
  }

  /**
   * Getter of Ticket.id.
   *
   * @return ticket identifier
   */
  public int getId() {
    return id;
  }

  /**
   * Setter of Ticket.id.
   *
   * @param idTicket the ticket identifier to set on the ticket
   */
  public void setId(final int idTicket) {
    this.id = idTicket;
  }

  /**
   * Getter of Ticket.parkingSpot.
   *
   * @return parkingSpot instance
   */
  public ParkingSpot getParkingSpot() {
    return parkingSpot;
  }

  /**
   * Setter of Ticket.parkingSpot.
   *
   * @param spot instance of ParkingSpot to set on the ticket
   */
  public void setParkingSpot(final ParkingSpot spot) {
    this.parkingSpot = spot;
  }

  /**
   * Getter of Ticket.parkingSpot.
   *
   * @return the vehicle registration number associated
   *     to a given ticket
   */
  public String getVehicleRegNumber() {
    return vehicleRegNumber;
  }

  /**
   * Setter of Ticket.parkingSpot.
   *
   * @param regNumber the vehicle registration number to set on the ticket
   */
  public void setVehicleRegNumber(final String regNumber) {
    this.vehicleRegNumber = regNumber;
  }

  /**
   * Getter of Ticket.price.
   *
   * @return the price to pay when exit parking
   */
  public double getPrice() {
    return price;
  }

  /**
   * Setter of Ticket.price.
   *
   * @param priceToPay The price to set on the ticket
   */
  public void setPrice(final double priceToPay) {
    this.price = priceToPay;
  }

  /**
   * Getter of Ticket.inTime.
   *
   * @return the LocalDateTime of parking entry
   */
  public LocalDateTime getInTime() {
    return inTime;
  }

  /**
   * Setter of Ticket.inTime.
   *
   * @param entryTime the entry LocalDateTime to set on the ticket
   */
  public void setInTime(final LocalDateTime entryTime) {
    this.inTime = entryTime;
  }

  /**
   * Getter of Ticket.outTime.
   *
   * @return the LocalDateTime of parking exit
   */
  public LocalDateTime getOutTime() {
    return outTime;
  }

  /**
   * Setter of Ticket.outTime.
   *
   * @param exitTime the exit LocalDateTime to set on the ticket
   */
  public void setOutTime(final LocalDateTime exitTime) {
    this.outTime = exitTime;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ticket ticket = (Ticket) o;
    return id == ticket.id
      && Double.compare(ticket.price, price) == 0
      && parkingSpot.equals(ticket.parkingSpot)
      && vehicleRegNumber.equals(ticket.vehicleRegNumber)
      && inTime.equals(ticket.inTime)
      && Objects.equals(outTime, ticket.outTime);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(id, parkingSpot, vehicleRegNumber, price,
    inTime, outTime);
  }
}
