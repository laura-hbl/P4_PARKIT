package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DataBaseParameters;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @Tag("GetNextAvailableSpot")
    @DisplayName("Given an expected available parking spot, when getNextAvailableSpot, then nextAvailableSpot should match expected")
    public void givenParkingTypeCar_whenGetNextAvailableSpot_thenNextAvailableSpotMatchesExpected() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(DataBaseParameters.ONE)).thenReturn(2);

        int nextAvailableSpot = parkingSpotDao.getNextAvailableSpot(ParkingType.CAR);

        InOrder inOrder = inOrder(preparedStatement, resultSet);
        inOrder.verify(preparedStatement).setString(DataBaseParameters.ONE, ParkingType.CAR.toString());
        inOrder.verify(preparedStatement).executeQuery();
        inOrder.verify(resultSet).next();
        inOrder.verify(resultSet).getInt(DataBaseParameters.ONE);
        assertThat(nextAvailableSpot).isEqualTo(2);
    }

    @Test
    @Tag("GetNextAvailableSpot")
    @DisplayName("Given no available parking spot, when getNextAvailableSpot, then getNextAvailableSpot should returns -1")
    public void givenParkingTypeCar_whenGetNextAvailableSpotIsInvalid_thenNegativeResultReturned() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        int nextAvailableSpot = parkingSpotDao.getNextAvailableSpot(ParkingType.CAR);

        assertThat(nextAvailableSpot).isEqualTo(-1);
    }

    @Test
    @Tag("UpdateParking")
    @DisplayName("Given a parking spot, when updateParking, then parking spot should be updated")
    public void givenAParkingSpot_whenUpdateParking_thenParkingSpotIsUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        boolean isParkingUpdated = parkingSpotDao.updateParking(parkingSpot);

        InOrder inOrder = inOrder(preparedStatement);
        inOrder.verify(preparedStatement).setBoolean(DataBaseParameters.ONE,parkingSpot.isAvailable());
        inOrder.verify(preparedStatement).setInt(DataBaseParameters.TWO, parkingSpot.getNumber());
        inOrder.verify(preparedStatement).executeUpdate();
        assertThat(isParkingUpdated).isEqualTo(true);
    }

    @Test
    @Tag("UpdateParking")
    @DisplayName("Given a parking spot, when updateParking is not executed, then parking spot should not be updated")
    public void givenAParkingSpot_whenUpdateParkingNotExecuted_thenParkingSpotNotUpdated() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        boolean isParkingUpdated = parkingSpotDao.updateParking(parkingSpot);

        assertThat(isParkingUpdated).isEqualTo(false);
    }
}
