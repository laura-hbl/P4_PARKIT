package com.parkit.parkingsystem.unit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.FareCalculatorHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorHelperTest {

    private Ticket ticket;

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void givenAOneHourCarTicket_whenGetCalculatedFare_thenFareIsEqualToCarRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        double calculateFare = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(calculateFare).isEqualTo(Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void givenAOneHourCarTicket_whenGetCalculatedFareWithDiscount_thenRateIncludesDiscount() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        double calculateFare = FareCalculatorHelper.getCalculatedFare(ticket, Fare.DISCOUNT);

        assertThat(calculateFare).isEqualTo(Fare.CAR_RATE_PER_HOUR - (Fare.DISCOUNT * Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    public void givenAOneHourBikeTicket_whenGetCalculatedFare_thenFareIsEqualToBikeRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        double calculateFare = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(calculateFare).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void givenAOneHourBikeTicket_whenGetCalculatedFareWithDiscount_thenRateIncludesDiscount() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());
        ticket.setPrice(Fare.DISCOUNT);

        double calculateFare = FareCalculatorHelper.getCalculatedFare(ticket, Fare.DISCOUNT);

        assertThat(calculateFare).isEqualTo(Fare.BIKE_RATE_PER_HOUR - (Fare.DISCOUNT * Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    public void givenALessThanOneHourCarTicket_whenGetCalculatedFare_thenFareIsThreeQuarterTheCarRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(45));
        ticket.setOutTime(LocalDateTime.now());

        double calculateFare = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(calculateFare).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void givenALessThanOneHourBikeTicket_whenGetCalculatedFare_thenFareIsThreeQuarterTheBikeRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(45));
        ticket.setOutTime(LocalDateTime.now());

        double calculateFare = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(calculateFare).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void givenAOneDayCarTicket_whenGetCalculatedFare_thenFareIs24TimesTheCarRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(LocalDateTime.now().plusMinutes(1440));

        double fare = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(fare).isEqualTo(24 * Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void givenAOneDayBikeTicket_whenGetCalculatedFare_thenFareIs24TimesTheBikeRatePerHour() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(LocalDateTime.now().plusMinutes(1440));

        double calculateFare = FareCalculatorHelper.getCalculatedFare(ticket, 0);

        assertThat(calculateFare).isEqualTo(24 * Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void givenANullParkingTypeTicket_whenGetCalculatedFare_thenNullPointerExceptionThrown() {
        ticket.setParkingSpot(new ParkingSpot(1, null, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        assertThrows(NullPointerException.class, () -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }

    @Test
    public void givenCarTicketWithNoOutTime_whenGetCalculatedFare_thenIllegalArgumentExceptionThrown() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().minusMinutes(60));
        ticket.setOutTime(null);

        assertThrows(IllegalArgumentException.class, () -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }

    @Test
    public void givenBikeTicketWithNoOutTime_whenGetCalculatedFare_thenIllegalArgumentExceptionThrown() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now());
        ticket.setOutTime(null);

        assertThrows(IllegalArgumentException.class, () -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }

    @Test
    public void givenCarTicketWithFutureInTime_whenGetCalculatedFare_thenIllegalArgumentExceptionThrown() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(LocalDateTime.now().plusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }

    @Test
    public void givenBikeTicketWithFutureInTime_whenGetCalculatedFare_thenIllegalArgumentExceptionThrown() {
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.BIKE, false));
        ticket.setInTime(LocalDateTime.now().plusMinutes(60));
        ticket.setOutTime(LocalDateTime.now());

        assertThrows(IllegalArgumentException.class, () -> FareCalculatorHelper.getCalculatedFare(ticket, 0));
    }

}