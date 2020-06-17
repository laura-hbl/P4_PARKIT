package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDaoTest {

    private static ParkingSpotDao parkingSpotDao = new ParkingSpotDao();

    @Mock
    private static DataBaseConfig dataBaseConfig;
    @Mock
    private static Connection connection;
    @Mock
    private static PreparedStatement preparedStatement;
    @Mock
    private static ResultSet resultSet;


    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(dataBaseConfig.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(any(String.class))).thenReturn(preparedStatement);
        parkingSpotDao.setDataBaseConfig(dataBaseConfig);
    }

    @Test
    public void givenParkingTypeCar_whenGetNextAvailableSpot_thenNextAvailableSpotMatchesExpected() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(2);

        int nextAvailableSpot = parkingSpotDao.getNextAvailableSpot(ParkingType.CAR);

        verify(preparedStatement).setString(1, ParkingType.CAR.toString());
        verify(preparedStatement).executeQuery();
        verify(resultSet).next();
        verify(resultSet).getInt(1);
        assertThat(nextAvailableSpot).isEqualTo(2);
    }

    @Test
    public void givenParkingTypeCar_whenGetNextAvailableSpotIsInvalid_thenNegativeResultReturned() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        int nextAvailableSpot = parkingSpotDao.getNextAvailableSpot(ParkingType.CAR);

        assertThat(nextAvailableSpot).isEqualTo(-1);
    }

    @Test
    public void givenAParkingSpot_whenUpdateParking_thenParkingSpotIsUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isParkingUpdated = parkingSpotDao.updateParking(parkingSpot);

        InOrder inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setBoolean(1,parkingSpot.isAvailable());
        inOrder.verify(preparedStatement).setInt(2, parkingSpot.getNumber());
        inOrder.verify(preparedStatement).executeUpdate();
        assertThat(isParkingUpdated).isEqualTo(true);
    }

    @Test
    public void givenAParkingSpot_whenUpdateParkingNotExecuted_thenParkingSpotNotUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean isParkingUpdated = parkingSpotDao.updateParking(parkingSpot);

        assertThat(isParkingUpdated).isEqualTo(false);
    }
}
