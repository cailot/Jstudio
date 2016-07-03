package kr.co.tmoney.mobiledriverconsole.model.vo;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class VehicleVO {
    private String vehicleId;
    private int accuracy;
    private String currentRoute;
    private int currentStopIndex;
    private int heading;
    private double latitude;
    private double longitude;
    private int passenger;
    private String rearVehicle;
    private double speed;
    private boolean tripOn;
    private long updated;

    public VehicleVO() {
    }

    public VehicleVO(int accuracy, String currentRoute, int currentStopIndex, int heading, double latitude, double longitude, int passenger, String rearVehicle, double speed, boolean tripOn, long updated, String vehicleId) {
        this.accuracy = accuracy;
        this.currentRoute = currentRoute;
        this.currentStopIndex = currentStopIndex;
        this.heading = heading;
        this.latitude = latitude;
        this.longitude = longitude;
        this.passenger = passenger;
        this.rearVehicle = rearVehicle;
        this.speed = speed;
        this.tripOn = tripOn;
        this.updated = updated;
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "VehicleVO{" +
                "accuracy=" + accuracy +
                ", vehicleId='" + vehicleId + '\'' +
                ", currentRoute='" + currentRoute + '\'' +
                ", currentStopIndex=" + currentStopIndex +
                ", heading=" + heading +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", passenger=" + passenger +
                ", rearVehicle='" + rearVehicle + '\'' +
                ", speed=" + speed +
                ", tripOn=" + tripOn +
                ", updated=" + updated +
                '}';
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

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPassenger() {
        return passenger;
    }

    public void setPassenger(int passenger) {
        this.passenger = passenger;
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