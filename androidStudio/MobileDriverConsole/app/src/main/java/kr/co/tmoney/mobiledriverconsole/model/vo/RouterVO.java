package kr.co.tmoney.mobiledriverconsole.model.vo;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class RouterVO {
    private int stopId;
    private String stopName;

    public RouterVO(int stopId, String stopName) {
        this.stopId = stopId;
        this.stopName = stopName;
    }

    public RouterVO() {
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    @Override
    public String toString() {
        return "RouterVO{" +
                "stopId=" + stopId +
                ", stopName='" + stopName + '\'' +
                '}';
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }
}
