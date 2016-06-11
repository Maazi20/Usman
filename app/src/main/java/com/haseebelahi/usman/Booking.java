package com.haseebelahi.usman;
/**
 * Created by Haseeb Elahi on 4/12/2016.
 */
public class Booking {
    private String Id, user, driver, source, destination, sourceAddress, destAddress, note, fare, distance, status,service,gender,car;

    public Booking() {
        setId("");
        setUser("");
        setDestination("");
        setDriver("");
        setDestAddress("");
        setSourceAddress("");
        setFare("");
        setSource("");
        setStatus("0");
        setNote("");
        setService("");
        setGender("");
        setCar("");
    }

    public void setId(String id) {
        Id = id;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return Id;
    }

    public String getUser() {
        return user;
    }

    public String getDriver() {
        return driver;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public String getFare() {
        return fare;
    }

    public String getDistance() {
        return distance;
    }

    public void setService(String Service) {
        this.service = Service;
    }
    public void setGender(String Gender) {
        this.gender = Gender;
    }
    public void setCar(String Car) {
        this.car = Car;
    }

    public String getService() {
        return service;
    }
    public String getGender() {
        return gender;
    }
    public String getCar() {
        return car;
    }
}
