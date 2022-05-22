package com.example.task101;

import java.io.Serializable;

public class Order implements Serializable {

    // order data variables
    private byte[] goodImageByte;
    private String receiverName, date, time, location, destination, goodType, vehicleType;
    private String weight, width, length, height;
    private double locationLatitude, locationLongitude, destinationLatitude, destinationLongitude;
    // the logged in user which made the order
    private String username;

    // constructor to create the complete order object
    public Order(String username, byte[] goodImageByte,String receiverName, String date, String time, String location, double locationLatitude, double locationLongitude, String destination, double destinationLatitude, double destinationLongitude, String goodType, String vehicleType, String weight, String length, String height, String width) {
        this.username = username;
        this.goodImageByte = goodImageByte;
        this.receiverName = receiverName;
        this.date = date;
        this.time = time;
        this.location = location;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.destination = destination;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.goodType = goodType;
        this.vehicleType = vehicleType;
        this.weight = weight;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    // empty constructor, order details will be added with setters
    public Order() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getGoodImageByte() {
        return goodImageByte;
    }

    public void setGoodImageByte(byte[] goodImageByte) {
        this.goodImageByte = goodImageByte;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
