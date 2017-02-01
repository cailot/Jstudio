package com.creapple.tms.mobiledriverconsole.model.vo;

import java.io.Serializable;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class StopVO implements Serializable, Comparable<StopVO>{
    private String direction;
    private int fareStopTag;
    private String id;
    private double lon;
    private double lat;
    private String name;
    private int sortIndex;
    private String type;

    public StopVO() {
    }

    @Override
    public String toString() {
        return "StopVO{" +
                "direction='" + direction + '\'' +
                ", fareStopTag=" + fareStopTag +
                ", id=" + id +
                ", lon=" + lon +
                ", lat=" + lat +
                ", name='" + name + '\'' +
                ", sortIndex=" + sortIndex +
                ", type='" + type + '\'' +
                '}';
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getFareStopTag() {
        return fareStopTag;
    }

    public void setFareStopTag(int fareStopTag) {
        this.fareStopTag = fareStopTag;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StopVO(String direction, int fareStopTag, String id, double lat, double lon, String name, int sortIndex, String type) {
        this.direction = direction;
        this.fareStopTag = fareStopTag;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.sortIndex = sortIndex;
        this.type = type;
    }

    /**
     * This method will be used for sorting StopVO array by sortIndex
     * @param stopVO
     * @return
     */
    @Override
    public int compareTo(StopVO stopVO) {
        int compareIndex = ((StopVO)stopVO).getSortIndex();
        return this.sortIndex - compareIndex;
    }
}
