package kr.co.tmoney.mobiledriverconsole.model.vo;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class StopVO {
    private String direction;
    private int fareStopTag;
    private int stopId;
    private double latitude;
    private double longitude;
    private String name;
    private int sortIndex;
    private String type;

    public StopVO() {
    }

    public StopVO(String direction, int fareStopTag, int id, double latitude, double longitude, String name, int sortIndex, String type) {
        this.direction = direction;
        this.fareStopTag = fareStopTag;
        this.stopId = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.sortIndex = sortIndex;
        this.type = type;
    }

    @Override
    public String toString() {
        return "StopVO{" +
                "direction='" + direction + '\'' +
                ", fareStopTag=" + fareStopTag +
                ", id=" + stopId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
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

    public int getId() {
        return stopId;
    }

    public void setId(int id) {
        this.stopId = id;
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
}
