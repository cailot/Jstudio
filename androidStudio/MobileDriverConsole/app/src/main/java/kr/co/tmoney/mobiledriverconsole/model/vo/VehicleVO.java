package kr.co.tmoney.mobiledriverconsole.model.vo;

import java.io.Serializable;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class VehicleVO implements Serializable{
    private int accuracy;
    private String currentRoute;
    private int currentStopIndex;
    private String currentStopName;
    private int heading;
    private String id;
    private double lat;
    private double lon;
    private int passengers;
    private String frontVehicle;
    private String rearVehicle;
    private double speed;
    private boolean tripOn;
    private String updated;

    @Override
    public String toString() {
        return "VehicleVO{" +
                "accuracy=" + accuracy +
                ", currentRoute='" + currentRoute + '\'' +
                ", currentStopIndex=" + currentStopIndex +
                ", currentStopName='" + currentStopName + '\'' +
                ", heading=" + heading +
                ", id='" + id + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", passengers=" + passengers +
                ", frontVehicle='" + frontVehicle + '\'' +
                ", rearVehicle='" + rearVehicle + '\'' +
                ", speed=" + speed +
                ", tripOn=" + tripOn +
                ", updated='" + updated + '\'' +
                '}';
    }

    public VehicleVO() {
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String getCurrentRoute() {
        return currentRoute;
    }

    public void setCurrentRoute(String currentRoute) {
        this.currentRoute = currentRoute;
    }

    public int getCurrentStopIndex() {
        return currentStopIndex;
    }

    public void setCurrentStopIndex(int currentStopIndex) {
        this.currentStopIndex = currentStopIndex;
    }

    public String getCurrentStopName() {
        return currentStopName;
    }

    public void setCurrentStopName(String currentStopName) {
        this.currentStopName = currentStopName;
    }

    public String getFrontVehicle() {
        return frontVehicle;
    }

    public void setFrontVehicle(String frontVehicle) {
        this.frontVehicle = frontVehicle;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getRearVehicle() {
        return rearVehicle;
    }

    public void setRearVehicle(String rearVehicle) {
        this.rearVehicle = rearVehicle;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isTripOn() {
        return tripOn;
    }

    public void setTripOn(boolean tripOn) {
        this.tripOn = tripOn;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public VehicleVO(int accuracy, String currentRoute, int currentStopIndex, String currentStopName, String frontVehicle, int heading, String id, double lat, double lon, int passengers, String rearVehicle, double speed, boolean tripOn, String updated) {
        this.accuracy = accuracy;
        this.currentRoute = currentRoute;
        this.currentStopIndex = currentStopIndex;
        this.currentStopName = currentStopName;
        this.frontVehicle = frontVehicle;
        this.heading = heading;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.passengers = passengers;
        this.rearVehicle = rearVehicle;
        this.speed = speed;
        this.tripOn = tripOn;
        this.updated = updated;
    }
}