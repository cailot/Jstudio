package com.creapple.tms.mobiledriverconsole.model.vo;

import java.io.Serializable;

/**
 * Created by jinseo on 2016. 7. 7..
 */
public class TripVO implements Serializable{

    private String currentStopId;

    private String currentStopName;

    private String driverId;

    private String route;

    private String vehicleId;

    private String status;

    private long updated;

    private int stopDuration;

    private int driveDuration;

    private double averageSpeed;

    private int passengerCount;

    private int cashAmount;

    private int totalPassengerCount;

    private int totalCashAmount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDriveDuration() {
        return driveDuration;
    }

    public void setDriveDuration(int driveDuration) {
        this.driveDuration = driveDuration;
    }

    public int getStopDuration() {
        return stopDuration;
    }

    public void setStopDuration(int stopDuration) {
        this.stopDuration = stopDuration;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(int cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getCurrentStopId() {
        return currentStopId;
    }

    public void setCurrentStopId(String currentStopId) {
        this.currentStopId = currentStopId;
    }

    public String getCurrentStopName() {
        return currentStopName;
    }

    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getTotalCashAmount() {
        return totalCashAmount;
    }

    public void setTotalCashAmount(int totalCashAmount) {
        this.totalCashAmount = totalCashAmount;
    }

    public TripVO() {
    }

    public int getTotalPassengerCount() {
        return totalPassengerCount;
    }

    public void setTotalPassengerCount(int totalPassengerCount) {
        this.totalPassengerCount = totalPassengerCount;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }


}
