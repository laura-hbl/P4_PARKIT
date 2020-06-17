package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DataBaseConstants;
import com.parkit.parkingsystem.constants.DataBaseParameters;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Contains methods that allow interaction between the system and the
 * database(parking table of the prod DB).
 *
 * @author Laura
 */
public class ParkingSpotDao {

  /**
   * ParkingSpotDao logger.
   */
  private static final Logger LOGGER = LogManager.getLogger("ParkingSpotDao");

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
   * Checks in database whether there is an available parking spot for the given
   * vehicle type.
   *
   * @param parkingType the user's vehicle type (bike or car)
   * @return the available parking spot number or -1 if no parking spot
   *     is available for this type of vehicle
   */
  public int getNextAvailableSpot(final ParkingType parkingType) {
    try (Connection con = dataBaseConfig.getConnection();
         PreparedStatement ps = con.prepareStatement(DataBaseConstants
            .GET_NEXT_PARKING_SPOT)) {
      ps.setString(DataBaseParameters.ONE, parkingType.toString());

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt(DataBaseParameters.ONE);
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Error fetching next available spot", ex);
    }

    return -1;
  }

  /**
   * Updates the availability of a parking spot.
   *
   * @param parkingSpot the parking spot that will be updated
   * @return true if the parking spot was updated successfully
   *     false if the update failed
   */
  public boolean updateParking(final ParkingSpot parkingSpot) {

    try (Connection con = dataBaseConfig.getConnection();
         PreparedStatement ps = con.prepareStatement(DataBaseConstants
            .UPDATE_PARKING_SPOT)) {
      ps.setBoolean(DataBaseParameters.ONE, parkingSpot.isAvailable());
      ps.setInt(DataBaseParameters.TWO, parkingSpot.getNumber());

      if (ps.executeUpdate() == 1) {
        return true;
      }
    } catch (Exception ex) {
      LOGGER.error("Error updating parking info", ex);
    }

    return false;
  }
}
