package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private LocalDateTime inTime;
    private LocalDateTime outTime;

    public Ticket(int id, ParkingSpot parkingSpot, String vehicleRegNumber, double price, LocalDateTime inTime, LocalDateTime outTime) {
        this.id = id;
        this.parkingSpot = parkingSpot;
        this.vehicleRegNumber = vehicleRegNumber;
        this.price = price;
        this.inTime = inTime;
        this.outTime = outTime;
    }

    public Ticket() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id == ticket.id &&
                Double.compare(ticket.price, price) == 0 &&
                parkingSpot.equals(ticket.parkingSpot) &&
                vehicleRegNumber.equals(ticket.vehicleRegNumber) &&
                inTime.equals(ticket.inTime) &&
                Objects.equals(outTime, ticket.outTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parkingSpot, vehicleRegNumber, price, inTime, outTime);
    }
}
