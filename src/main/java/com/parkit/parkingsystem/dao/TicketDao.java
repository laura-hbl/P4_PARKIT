package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DataBaseConstants;
import com.parkit.parkingsystem.constants.DataBaseParameters;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains methods that allow interaction between the system and
 * the database(ticket table of the prod DB).
 *
 * @author Laura
 */
public class TicketDao {

  /**
   * TicketDao logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("TicketDao");

  /**
   * Creates a dataBaseConfig instance to connect with the Prod DataBase.
   */
  private DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * Setter of a DataBaseConfig object.
   *
   * @param config the dataBaseConfig instance to set
   */
  public void setDataBaseConfig(final DataBaseConfig config) {
    this.dataBaseConfig = config;
  }

  /**
   * Saves given ticket to database.
   *
   * @param ticket the ticket to be saved
   * @return true if ticket was saved successfully
   *     false if the saving process failed
   */
  public boolean saveTicket(final Ticket ticket) {

    try (Connection con = dataBaseConfig.getConnection();
         PreparedStatement ps = con.prepareStatement(DataBaseConstants
            .SAVE_TICKET)) {
      // PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setInt(DataBaseParameters.ONE, ticket.getParkingSpot().getNumber());
      ps.setString(DataBaseParameters.TWO, ticket.getVehicleRegNumber());
      ps.setDouble(DataBaseParameters.THREE, ticket.getPrice());
      ps.setObject(DataBaseParameters.FOUR, ticket.getInTime());
      ps.setObject(DataBaseParameters.FIVE, ticket.getOutTime());

      if (ps.execute()) {
        return true;
      }
    } catch (Exception ex) {
      LOGGER.error("Error saving ticket", ex);
    }

    return false;
  }

  /**
   * Retrieves the latest ticket saved in database with the given
   *   licence plate number.
   *
   * @param vehicleRegNumber the user's vehicle registration number
   * @return the ticket found in database
   */
  public Ticket getTicket(final String vehicleRegNumber) {
    Ticket ticket = null;

    try (Connection con = dataBaseConfig.getConnection();
         PreparedStatement ps = con.prepareStatement(DataBaseConstants
            .GET_TICKET)) {
      //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setString(DataBaseParameters.ONE, vehicleRegNumber);

      try (ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
          ticket = new Ticket();
          ParkingSpot spot = new ParkingSpot(rs.getInt(DataBaseParameters.ONE),
              ParkingType.valueOf(rs.getString(DataBaseParameters.SIX)), false);
          ticket.setParkingSpot(spot);
          ticket.setId(rs.getInt(DataBaseParameters.TWO));
          ticket.setVehicleRegNumber(vehicleRegNumber);
          ticket.setPrice(rs.getDouble(DataBaseParameters.THREE));
          ticket.setInTime(rs.getTimestamp(DataBaseParameters.FOUR)
              .toLocalDateTime());
          Timestamp outTime = rs.getTimestamp(DataBaseParameters.FIVE);
          ticket.setOutTime(outTime != null ? outTime.toLocalDateTime() : null);
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Error getting ticket", ex);
    }

    return ticket;
  }

  /**
   * Updates given ticket with the price and outTime.
   *
   * @param ticket the ticket that should be updated
   * @return true if the ticket was updated successfully
   *     false if the updating process failed
   */
  public boolean updateTicket(final Ticket ticket) {
    try (Connection con = dataBaseConfig.getConnection();
         PreparedStatement ps = con.prepareStatement(DataBaseConstants
            .UPDATE_TICKET)) {
      ps.setDouble(DataBaseParameters.ONE, ticket.getPrice());
      ps.setObject(DataBaseParameters.TWO, ticket.getOutTime());
      ps.setInt(DataBaseParameters.THREE, ticket.getId());

      if (ps.executeUpdate() == 1) {
        return true;
      }
    } catch (Exception ex) {
      LOGGER.error("Error updating ticket", ex);
    }

    return false;
  }

  /**
   * Checks if the incoming user had already used the parking.
   *
   * @param vehicleRegNumber the user's vehicle registration number
   * @return true if the user had already used the parking
   *     false if the user enters the parking for the first time
   */
  public boolean isRecurringUser(final String vehicleRegNumber) {
    try (Connection con = dataBaseConfig.getConnection();
         PreparedStatement ps = con.prepareStatement(DataBaseConstants
            .GET_TICKET_COUNT)) {
      ps.setString(DataBaseParameters.ONE, vehicleRegNumber);

      try (ResultSet rs = ps.executeQuery()) {
        /* If the count is greater than 1 it means there are more than 1 ticket
           associated with this user.
           If it is equal to one then the user has entered the parking for the
           first time, but has not yet exited. */
        if (rs.next() && rs.getInt(DataBaseParameters.ONE) > 1) {
          return true;
        } else {
          LOGGER.info("No ticket found with this registration number");
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Error checking ticket", ex);
    }

    return false;
  }
}
