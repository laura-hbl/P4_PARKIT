package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig;

    public TicketDAO(DataBaseConfig dataBaseConfig) {
        this.dataBaseConfig = dataBaseConfig;
    }

    public boolean saveTicket(Ticket ticket) {
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {
            // PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setInt(1, ticket.getParkingSpot().getNumber());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setObject(4, ticket.getInTime());
            ps.setObject(5, ticket.getOutTime());

            if (ps.execute()) {
                return true;
            }
        } catch (Exception ex) {
            logger.error("Error saving ticket", ex);
        }

        return false;
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Ticket ticket = null;

        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, vehicleRegNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ticket = new Ticket();
                    ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
                    ticket.setParkingSpot(parkingSpot);
                    ticket.setId(rs.getInt(2));
                    ticket.setVehicleRegNumber(vehicleRegNumber);
                    ticket.setPrice(rs.getDouble(3));
                    ticket.setInTime(rs.getTimestamp(4).toLocalDateTime());
                    Timestamp outTime = rs.getTimestamp(5);
                    ticket.setOutTime(outTime != null ? outTime.toLocalDateTime() : null);
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting ticket", ex);
        }

        return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)) {
            ps.setDouble(1, ticket.getPrice());
            ps.setObject(2, ticket.getOutTime());
            ps.setInt(3, ticket.getId());

            if (ps.executeUpdate() == 1) {
                return true;
            }
        } catch (Exception ex) {
            logger.error("Error updating ticket", ex);
        }

        return false;
    }

    public boolean isRecurringUser(String vehicleRegNumber) {
        try (Connection con = dataBaseConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET_COUNT)) {
            ps.setString(1, vehicleRegNumber);

            try (ResultSet rs = ps.executeQuery()) {
                // If the count is greater than 1 it means there are more than 1 ticket associated with this user.
                // If it is equal to one then the user has entered the parking for the first time, but has not yet exited.
                if (rs.next() && rs.getInt(1) > 1) {
                    return true;
                } else {
                    logger.info("No ticket found with this registration number");
                }
            }
        } catch (Exception ex) {
            logger.error("Error checking ticket", ex);
        }

        return false;
    }
}
