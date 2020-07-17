package com.parkit.parkingsystem.constants;

/**
 * Contains all SQL queries that allow exchange between the application
 * and database.
 *
 * @author Laura
 */
public final class DataBaseConstants {

  /**
   * Empty constructor of class DataBaseConstants.
   */
  private DataBaseConstants() {
  }

  /**
   * SQL query to find an available parking spot.
   */
  public static final String GET_NEXT_PARKING_SPOT = "select "
      + "min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";

  /**
   * SQL query to update availability of a parking spot.
   */
  public static final String UPDATE_PARKING_SPOT = "update parking set "
      + "available = ? where PARKING_NUMBER = ?";

  /**
   * SQL query to save a ticket in database.
   */
  public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, "
      + "VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";

  /**
   * SQL query to update a ticket in database.
   */
  public static final String UPDATE_TICKET = "update ticket set PRICE=?, "
      + "OUT_TIME=? where ID=?";

  /**
   * SQL query to get a ticket of database.
   */
  public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID,"
      + " t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where "
      + "p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order "
      + " by t.OUT_TIME limit 1";

  /**
   * SQL query to count ticket with the same registration number in database.
   */
  public static final String GET_TICKET_COUNT = "select count(t.ID) from "
      + "ticket t,parking p where p.PARKING_NUMBER = t.PARKING_NUMBER and "
      + "t.VEHICLE_REG_NUMBER=? and OUT_TIME is not null";
}
