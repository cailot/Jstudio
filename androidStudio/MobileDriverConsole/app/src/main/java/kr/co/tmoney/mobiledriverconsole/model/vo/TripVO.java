package kr.co.tmoney.mobiledriverconsole.model.vo;

import java.util.HashMap;

/**
 * Created by jinseo on 2016. 7. 7..
 */
public class TripVO {
    private String currentStopId;

    private String currentStopName;

    private String driverId;

    private HashMap<String, String> messagelogs;

    private String route;

    private String routeKey;

    private int runningNo;

    private HashMap<String, Object> stoplogs;

    private HashMap<String, Object> transactions;

    private long tripStartTime;

    private long updated;

    private String vehicleId;

    public TripVO() {
    }

    public TripVO(String currentStopId, String currentStopName, String driverId, HashMap<String, String> messagelogs, String route, String routeKey, int runningNo, HashMap<String, Object> stoplogs, HashMap<String, Object> transactions, long tripStartTime, long updated, String vehicleId) {
        this.currentStopId = currentStopId;
        this.currentStopName = currentStopName;
        this.driverId = driverId;
        this.messagelogs = messagelogs;
        this.route = route;
        this.routeKey = routeKey;
        this.runningNo = runningNo;
        this.stoplogs = stoplogs;
        this.transactions = transactions;
        this.tripStartTime = tripStartTime;
        this.updated = updated;
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "TripVO{" +
                "currentStopId='" + currentStopId + '\'' +
                ", currentStopName='" + currentStopName + '\'' +
                ", driverId='" + driverId + '\'' +
                ", messagelogs=" + messagelogs +
                ", route='" + route + '\'' +
                ", routeKey='" + routeKey + '\'' +
                ", runningNo=" + runningNo +
                ", stoplogs=" + stoplogs +
                ", transactions=" + transactions +
                ", tripStartTime=" + tripStartTime +
                ", updated=" + updated +
                ", vehicleId='" + vehicleId + '\'' +
                '}';
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

    public HashMap<String, String> getMessagelogs() {
        return messagelogs;
    }

    public void setMessagelogs(HashMap<String, String> messagelogs) {
        this.messagelogs = messagelogs;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public int getRunningNo() {
        return runningNo;
    }

    public void setRunningNo(int runningNo) {
        this.runningNo = runningNo;
    }

    public HashMap<String, Object> getStoplogs() {
        return stoplogs;
    }

    public void setStoplogs(HashMap<String, Object> stoplogs) {
        this.stoplogs = stoplogs;
    }

    public HashMap<String, Object> getTransactions() {
        return transactions;
    }

    public void setTransactions(HashMap<String, Object> transactions) {
        this.transactions = transactions;
    }

    public long getTripStartTime() {
        return tripStartTime;
    }

    public void setTripStartTime(long tripStartTime) {
        this.tripStartTime = tripStartTime;
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
