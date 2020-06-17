package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.FareCalculatorHelper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.*;

public class FareCalculatorHelperTest {

    private Ticket ticket;

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a car parked one hour, the price should be equal to the car rate per hour")
    public void givenAOneHourCarTicket_whenGetCalculatedFare_thenFareIsEqualToCarRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a bike parked one hour, the price should be equal to the bike rate per hour")
    public void givenAOneHourBikeTicket_whenGetCalculatedFare_thenFareIsEqualToBikeRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a car parked 45 minutes, price should be equal 3/4 car rate per hour")
    public void givenALessThanOneHourCarTicket_whenGetCalculatedFare_thenFareIsThreeQuarterTheCarRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(45));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a bike parked 45 minutes, price should be equal 3/4 bike rate per hour")
    public void givenALessThanOneHourBikeTicket_whenGetCalculatedFare_thenFareIsThreeQuarterTheBikeRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(45));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a car parked 24 hours, price should be equal 24 multiply car rate per hour")
    public void givenAOneDayCarTicket_whenGetCalculatedFare_thenFareIs24TimesTheCarRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(LocalDateTime.now().plusMinutes(1440));

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(24 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a bike parked 24 hours, price should be equal 24 multiply bike rate per hour")
    public void givenAOneDayBikeTicket_whenGetCalculatedFare_thenFareIs24TimesTheBikeRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(LocalDateTime.now().plusMinutes(1440));

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(24 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a car parked half an hour, price should be equal half car rate per hour")
    public void givenAHalfHourCarTicket_whenGetCalculatedFare_thenFareIsHalfTheCarRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(30));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(0.5 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    @Tag("NormalRates")
    @DisplayName("For a bike parked half an hour, price should be equal half bike rate per hour")
    public void givenAHalfHourBikeTicket_whenGetCalculatedFare_thenFareIsHalfTheBikeRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(30));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(0.5 * Fare.BIKE_RATE_PER_HOUR);
    }

    @ParameterizedTest
    @Tag("FreeParking")
    @DisplayName("For a car parked less than 30 minutes, parking is free")
    @EnumSource(value = ParkingType.class, names = {"CAR", "BIKE"})
    public void givenALessThanHalfHourTicket_whenGetCalculatedFare_thenFareIsFree(ParkingType parkingType) {
        ticket.setParkingSpot(new ParkingSpot(1, parkingType, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(29));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(price).isEqualTo(0);
    }

    @Test
    @Tag("Discount")
    @DisplayName("For a recurrent user that parked a car one hour, price should be equal to car rate per hour with discount applied")
    public void givenAOneHourCarTicket_whenGetCalculatedFareWithDiscount_thenRateIncludesDiscount() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        double price = FareCalculatorHelper.getCalculatedFare(ticket, Fare.DISCOUNT);

        assertThat(price).isEqualTo(Fare.CAR_RATE_PER_HOUR - (Fare.DISCOUNT * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    @Tag("Discount")
    @DisplayName("For a recurrent user that parked a bike one hour, price should be equal to bike rate per hour with discount applied")
    public void givenAOneHourBikeTicket_whenGetCalculatedFareWithDiscount_thenRateIncludesDiscount() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());
        ticket.setPrice(Fare.DISCOUNT);

        double price = FareCalculatorHelper.getCalculatedFare(ticket, Fare.DISCOUNT);

        assertThat(price).isEqualTo(Fare.BIKE_RATE_PER_HOUR - (Fare.DISCOUNT * Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    @Tag("Exceptions")
    @DisplayName("If parking spot type is undefined, calculatorFare raise an NullPointerException")
    public void givenANullParkingTypeTicket_whenGetCalculatedFare_thenNullPointerExceptionThrown() {
        ticket.setParkingSpot(new ParkingSpot(1, null, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        assertThatNullPointerException().isThrownBy(() -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }

    @ParameterizedTest
    @Tag("Exceptions")
    @DisplayName("For a ticket with null out time, calculatorFare should raise an IllegalArgumentException")
    @EnumSource(value = ParkingType.class, names = {"CAR", "BIKE"})
    public void givenATicketWithNoOutTime_whenGetCalculatedFare_thenIllegalArgumentExceptionThrown(ParkingType parkingType) {
        ticket.setParkingSpot(new ParkingSpot(1, parkingType, false));
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(null);

        assertThatIllegalArgumentException().isThrownBy(() -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }

    @ParameterizedTest
    @Tag("Exceptions")
    @DisplayName("If parking exit time is prior to entry, calculatorFare should raise an IllegalArgumentException")
    @EnumSource(value = ParkingType.class, names = {"CAR", "BIKE"})
    public void givenATicketWithFutureInTime_whenGetCalculatedFare_thenIllegalArgumentExceptionThrown(ParkingType parkingType) {
        ticket.setParkingSpot(new ParkingSpot(1, parkingType, false));
        ticket.setInTime(LocalDateTime.now().plusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        assertThatIllegalArgumentException().isThrownBy(() -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }
}